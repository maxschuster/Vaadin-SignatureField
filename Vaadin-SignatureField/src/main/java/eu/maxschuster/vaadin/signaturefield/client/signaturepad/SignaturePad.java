/*
 * Copyright 2014 Max Schuster
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.maxschuster.vaadin.signaturefield.client.signaturepad;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A GWT Version of <code>SignaturePad</code> by
 * <b>Szymon Nowak (szimek)</b> that can be found at 
 * <a href="https://github.com/szimek/signature_pad">
 * https://github.com/szimek/signature_pad</a><br>
 * <br>
 * @author Max Schuster
 */
public class SignaturePad implements HasHandlers {

    private final Canvas canvas;

    private final Context2d context;
    
    private final List<Point> points = new ArrayList<Point>();
    
    private final HandlerManager handlerManager;
    
    private boolean empty = true;

    private double velocityFilterWeight = 0.7d;

    private double minWidth = 0.5d;

    private double maxWidth = 2.5d;

    private Double dotSize = null;

    private String penColor = "black";

    private String backgroundColor = "rgba(0,0,0,0)";
    
    private double lastVelocity = 0;
    
    private double lastWidth = 0;
    
    boolean mouseButtonDown = false;
    
    private boolean readOnly = false;

    public SignaturePad(Canvas canvas) {
        handlerManager = new HandlerManager(this);
        
        this.canvas = canvas;
        context = canvas.getContext2d();
        
        handleMouseEvents();
        handleTouchEvents();
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }
    
    public HandlerRegistration addBeginEventHandler(BeginEventHandler handler) {
        return handlerManager.addHandler(BeginEvent.TYPE, handler);
    }
    
    public HandlerRegistration addEndEventHandler(EndEventHandler handler) {
        return handlerManager.addHandler(EndEvent.TYPE, handler);
    }
    
    public void removeBeginEventHandler(BeginEventHandler handler) {
        handlerManager.removeHandler(BeginEvent.TYPE, handler);
    }
    
    public void removeEndEventHandler(EndEventHandler handler) {
        handlerManager.removeHandler(EndEvent.TYPE, handler);
    }

    public void clear() {
        context.setFillStyle(getBackgroundColor());
        context.clearRect(0d, 0d, 
                canvas.getCoordinateSpaceWidth(), 
                canvas.getCoordinateSpaceHeight());
        context.fillRect(0d, 0d, 
                canvas.getCoordinateSpaceWidth(), 
                canvas.getCoordinateSpaceHeight());
        reset();
    }
    
    public String toDataURL(String imageType, float quality) {
        return canvas.toDataUrl(imageType);
    }
    
    public void fromDataURL(String dataURL) {
        Double ratio = getDevicePixelWidth();
        if (ratio == null) {
            ratio = 1d;
        }
        final double width = canvas.getCoordinateSpaceWidth() / ratio;
        final double height = canvas.getCoordinateSpaceHeight() / ratio;
        SafeUri uri = UriUtils.fromTrustedString(dataURL);
        final Image image = new Image(uri);
        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                ImageElement imageElement = ImageElement.as(image.getElement());
                context.drawImage(imageElement, 0d, 0d, width, height);
                RootPanel.get().remove(image);
            }
        });
        RootPanel.get().add(image);
    }
    
    private void strokeUpdate(EventWrapper event) {
        if (isReadOnly()) {
            return;
        }
        /*
        Console.log(
                "update",
                "" + velocityFilterWeight, 
                "" + minWidth, 
                "" + maxWidth, 
                "" + getAppliedDotSize(), 
                "" + penColor, 
                "" + backgroundColor, 
                "" + lastVelocity, 
                "" + lastWidth);
        */
        Point point = createPoint(event);
        addPoint(point);
    };

    private void strokeBegin(EventWrapper event) {
        if (isReadOnly()) {
            return;
        }
        reset();
        strokeUpdate(event);
        fireEvent(new BeginEvent(this));
    };

    private void strokeDraw(Point point) {
        context.beginPath();
        drawPoint(point.getX(), point.getY(), getAppliedDotSize());
        context.closePath();
        context.fill();
    };

    private void strokeEnd(EventWrapper event) {
        boolean canDrawCurve = points.size() > 2;
        Point point = points.get(0);

        if (!canDrawCurve && point != null) {
            strokeDraw(point);
        }
        
        fireEvent(new EndEvent(this));
    };
    
    private void handleMouseEvents() {
        
        canvas.addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.preventDefault();
                canvas.setFocus(true);
                NativeEvent nEvent = event.getNativeEvent();
                if ((nEvent.getButton() & NativeEvent.BUTTON_LEFT) != 0) {
                    Event.setCapture(canvas.getElement());
                    mouseButtonDown = true;
                    strokeBegin(new EventWrapper(nEvent));
                }
            }
        });
        
        canvas.addMouseMoveHandler(new MouseMoveHandler() {

            @Override
            public void onMouseMove(MouseMoveEvent event) {
                event.preventDefault();
                if (mouseButtonDown) {
                    strokeUpdate(new EventWrapper(event.getNativeEvent()));
                }
            }
        });
        
        canvas.addMouseUpHandler(new MouseUpHandler() {

            @Override
            public void onMouseUp(MouseUpEvent event) {
                event.preventDefault();
                Event.releaseCapture(canvas.getElement());
                NativeEvent nEvent = event.getNativeEvent();
                if ((nEvent.getButton() & NativeEvent.BUTTON_LEFT) != 0 &&
                        mouseButtonDown) {
                    mouseButtonDown = false;
                    strokeEnd(new EventWrapper(nEvent));
                }
            }
        });
    }
    
    private void handleTouchEvents() {
        
        // Pass touch events to canvas element on mobile IE.
        canvas.getElement().getStyle().setProperty("-ms-touch-action", "none");
        
        canvas.addTouchStartHandler(new TouchStartHandler() {

            @Override
            public void onTouchStart(TouchStartEvent event) {
                Event.setCapture(event.getRelativeElement());
                event.preventDefault();
                Touch touch = event.getTouches().get(0);
                strokeBegin(new EventWrapper(touch));
            }
        });
        
        canvas.addTouchMoveHandler(new TouchMoveHandler() {

            @Override
            public void onTouchMove(TouchMoveEvent event) {
                event.preventDefault();
                Touch touch = event.getTouches().get(0);
                strokeUpdate(new EventWrapper(touch));
            }
        });
        
        canvas.addTouchEndHandler(new TouchEndHandler() {

            @Override
            public void onTouchEnd(TouchEndEvent event) {
                Event.setCapture(event.getRelativeElement());
                event.preventDefault();
                strokeEnd(new EventWrapper(event.getNativeEvent()));
            }
        });
    }

    public boolean isEmpty() {
        return empty;
    }
    
    private void reset() {
        points.clear();
        lastVelocity = 0;
        lastWidth = (minWidth + maxWidth) / 2;
        empty = true;
        context.setFillStyle(penColor);
    }
    
    private Point createPoint(EventWrapper event) {
        return new Point(
            event.getClientX() - canvas.getAbsoluteLeft(),
            event.getClientY() - canvas.getAbsoluteTop()
        );
    }
    
    private void addPoint(Point point) {
        Point c2;
        Point c3;
        Beizer curve;
        CurveControlPoints tmp;
        
        points.add(point);
        
        if (points.size() > 2) {
            // To reduce the initial lag make it work with 3 points
            // by copying the first point to the beginning.
            if (points.size() == 3) points.add(0, points.get(0));
            
            tmp = calculateCurveControlPoints(
                    points.get(0), points.get(1),points.get(2));
            c2 = tmp.getPoint2();
            tmp = calculateCurveControlPoints(
                    points.get(1), points.get(2),points.get(3));
            c3 = tmp.getPoint1();
            curve = new Beizer(points.get(1), c2, c3, points.get(2));
            addCurve(curve);
            
            // Remove the first element from the list,
            // so that we always have no more than 4 points in points array.
            points.remove(0);
        }
    }
    
    private CurveControlPoints calculateCurveControlPoints(Point s1, Point s2,
            Point s3) {
        double dx1 = s1.getX() - s2.getX(), dy1 = s1.getY() - s2.getY();
        double dx2 = s2.getX() - s3.getX(), dy2 = s2.getY() - s3.getY();
        
        Coordinates m1 = new Coordinates(
                (s1.getX() + s2.getX()) / 2d,
                (s1.getY() + s2.getY()) / 2d
            );
        Coordinates m2 = new Coordinates(
                (s2.getX() + s3.getX()) / 2d,
                (s2.getY() + s3.getY()) / 2d
            );
        
        double l1 = Math.sqrt(dx1*dx1 + dy1*dy1);
        double l2 = Math.sqrt(dx2*dx2 + dy2*dy2);
        
        double dxm = (m1.getX() - m2.getX());
        double dym = (m1.getY() - m2.getY());
        
        double k = l2 / (l1 + l2);
        Coordinates cm = new Coordinates(m2.getX() + dxm*k, m2.getY() + dym*k);
        
        double tx = s2.getX() - cm.getX();
        double ty = s2.getY() - cm.getY();
        
        return new CurveControlPoints(
            new Point(m1.getX() + tx, m1.getY() + ty),
            new Point(m2.getX() + tx, m2.getY() + ty)
        );
    }
    
    private void addCurve(Beizer curve) {
        Point startPoint = curve.getStartPoint();
        Point endPoint = curve.getEndPoint();
        
        double velocity = endPoint.velocityFrom(startPoint);
        velocity = velocityFilterWeight * velocity
            + (1 - velocityFilterWeight) * lastVelocity;
        
        double newWidth = strokeWidth(velocity);
        drawCurve(curve, lastWidth, newWidth);
        
        lastVelocity = velocity;
        lastWidth = newWidth;
    }
    
    private void drawPoint(double x, double y, double size) {
        context.moveTo(x, y);
        context.arc(x, y, size, 0, 2 * Math.PI, false);
        empty = false;
    }
    
    private void drawCurve(Beizer curve, double startWidth, double endWidth) {
        double widthDelta = endWidth - startWidth;
        double drawSteps = Math.floor(curve.getLength());
        double width;
        double t;
        double tt;
        double ttt;
        double u;
        double uu;
        double uuu;
        double x;
        double y;
        
        context.beginPath();
        for (int i = 0; i < drawSteps; i++) {
            // Calculate the Bezier (x, y) coordinate for this step.
            t = i / drawSteps;
            tt = t * t;
            ttt = tt * t;
            u = 1 - t;
            uu = u * u;
            uuu = uu * u;

            x = uuu * curve.getStartPoint().getX();
            x += 3 * uu * t * curve.getControl1().getX();
            x += 3 * u * tt * curve.getControl2().getX();
            x += ttt * curve.getEndPoint().getX();

            y = uuu * curve.getStartPoint().getY();
            y += 3 * uu * t * curve.getControl1().getY();
            y += 3 * u * tt * curve.getControl2().getY();
            y += ttt * curve.getEndPoint().getY();

            width = startWidth + ttt * widthDelta;
            drawPoint(x, y, width);
        }
        context.closePath();
        context.fill();
    }
    
    private double strokeWidth(double velocity) {
        return Math.max(maxWidth / (velocity + 1), minWidth);
    }

    protected double getAppliedDotSize() {
        if (dotSize != null) {
            return dotSize;
        }
        return (minWidth + maxWidth) / 2;
    }
    
    protected static native Double getDevicePixelWidth() /*-{
        return $wnd.devicePixelWidth;
    }-*/;

    public Canvas getCanvas() {
        return canvas;
    }

    public double getVelocityFilterWeight() {
        return velocityFilterWeight;
    }

    public void setVelocityFilterWeight(double velocityFilterWeight) {
        this.velocityFilterWeight = velocityFilterWeight;
    }

    public double getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(double minWidth) {
        this.minWidth = minWidth;
    }

    public double getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
    }

    public Double getDotSize() {
        return dotSize;
    }

    public void setDotSize(Double dotSize) {
        this.dotSize = dotSize;
    }

    public String getPenColor() {
        return penColor;
    }

    public void setPenColor(String penColor) {
        this.penColor = penColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    protected class Point implements Cloneable {
        
        private final double x;
        
        private final double y;
        
        private final long time;

        public Point(double x, double y, Long time) {
            this.x = x;
            this.y = y;
            this.time = time != null ? time : new Date().getTime();
        }
        
        public Point(double x, double y) {
            this(x, y, null);
        }
        
        public double velocityFrom(Point start) {
            return (getTime() != start.getTime()) ? distanceTo(start) / (getTime() - start.getTime()) : 1;
        }
        
        public double distanceTo(Point start) {
            return Math.sqrt(Math.pow(getX() - start.getX(), 2) + Math.pow(getY() - start.getY(), 2));
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public long getTime() {
            return time;
        }
        
    }

    protected class Beizer {
        
        private final Point startPoint;
        
        private final Point control1;
        
        private final Point control2;
        
        private final Point endPoint;

        public Beizer(Point startPoint, Point control1, Point control2, Point endPoint) {
            this.startPoint = startPoint;
            this.control1 = control1;
            this.control2 = control2;
            this.endPoint = endPoint;
        }
        
        public double getLength() {
            double steps = 10;
            double length = 0;
            double t;
            double cx;
            double cy;
            double px = 0d;
            double py = 0d;
            double xdiff;
            double ydiff;
            for (int i = 0; i <= steps; i++) {
                t = i / steps;
                cx = getPoint(t, startPoint.getX(), control1.getX(),
                        control2.getX(), endPoint.getX());
                cy = getPoint(t, startPoint.getY(), control1.getY(),
                        control2.getY(), endPoint.getY());
                if (i > 0) {
                    xdiff = cx - px;
                    ydiff = cy - py;
                    length += Math.sqrt(xdiff * xdiff + ydiff * ydiff);
                }
                px = cx;
                py = cy;
            }
            return length;
        }
        
        private double getPoint(double t, double start, double c1, double c2, 
                double end) {
            return      start * (1.0 - t) * (1.0 - t)  * (1.0 - t)
               + 3.0 *  c1    * (1.0 - t) * (1.0 - t)  * t
               + 3.0 *  c2    * (1.0 - t) * t          * t
               +        end   * t         * t          * t;
        }

        public Point getStartPoint() {
            return startPoint;
        }

        public Point getControl1() {
            return control1;
        }

        public Point getControl2() {
            return control2;
        }

        public Point getEndPoint() {
            return endPoint;
        }
        
    }
    
    protected class CurveControlPoints {
        
        private final Point point1;
        
        private final Point point2;

        public CurveControlPoints(Point point1, Point point2) {
            this.point1 = point1;
            this.point2 = point2;
        }

        public Point getPoint1() {
            return point1;
        }

        public Point getPoint2() {
            return point2;
        }
    
    }
    
    protected class Coordinates {
        
        private final double x;
        
        private final double y;

        public Coordinates(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
        
    }
    
    private class EventWrapper {
    
        private final double clientX;
        
        private final double clientY;

        public EventWrapper(double clientX, double clientY) {
            this.clientX = clientX;
            this.clientY = clientY;
        }
        
        public EventWrapper(Touch touch) {
            this(touch.getClientX(), touch.getClientY());
        }
        
        public EventWrapper(NativeEvent event) {
            this(event.getClientX(), event.getClientY());
        }

        public double getClientX() {
            return clientX;
        }

        public double getClientY() {
            return clientY;
        }
        
    }

}
