/*
 * Copyright 2015 Max Schuster.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.maxschuster.vaadin.signaturefield.converter;

import com.vaadin.data.util.converter.Converter;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlEncoding;
import eu.maxschuster.dataurl.DataUrlSerializer;
import eu.maxschuster.dataurl.IDataUrlSerializer;
import eu.maxschuster.vaadin.signaturefield.shared.MimeType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.Locale;

/**
 * A converter that converts from an RFC 2397 data url {@link String} to 
 * a {@code byte[]} and back.
 * 
 * @author Max Schuster
 */
public class StringToByteArrayConverter implements Converter<String, byte[]> {

    private static final long serialVersionUID = 1L;

    /**
     * Images are always encoded using Base64.
     */
    private final DataUrlEncoding encoding = DataUrlEncoding.BASE64;

    /**
     * An optional {@link MimeType} that is used while converting.
     * If it is {@code null} the converter will try to guess the 
     * {@link MimeType} while converting to {@link DataUrl}.
     */
    private final MimeType mimeType;

    /**
     * {@link IDataUrlSerializer} used to extract the binary contents of the
     * RFC 2397 data url {@link String}.
     */
    private final IDataUrlSerializer serializer;

    /**
     * Creates a new {@link StringToByteArrayConverter} with the given 
     * {@link IDataUrlSerializer} and {@link MimeType}.
     * 
     * @param serializer {@link IDataUrlSerializer} used to extract the binary contents of the
     * RFC 2397 data url {@link String}. Must not be {@code null}!
     * @param mimeType {@link MimeType} that is used while converting. Must not 
     * be {@code null}!
     * @throws NullPointerException If {@code serializer} or {@code mimeType} is
     * {@code null}
     */
    public StringToByteArrayConverter(IDataUrlSerializer serializer,
            MimeType mimeType) throws NullPointerException {
        if (serializer == null) {
            throw new NullPointerException("The searializer is mandatory and "
                    + "mustn't be null");
        }
        if (mimeType == null) {
            throw new NullPointerException("The mime Type is mandatory and "
                    + "mustn't be null inside this constructor");
        }
        this.serializer = serializer;
        this.mimeType = mimeType;
    }

    /**
     * Creates a new {@link StringToByteArrayConverter} with the given 
     * {@link IDataUrlSerializer} and automatic {@link MimeType} guessing.
     * 
     * @param serializer {@link IDataUrlSerializer} used to extract the binary contents of the
     * RFC 2397 data url {@link String}. Must not be {@code null}!
     * @throws NullPointerException If {@code serializer} is {@code null}
     */
    public StringToByteArrayConverter(IDataUrlSerializer serializer)
            throws NullPointerException {
        if (serializer == null) {
            throw new NullPointerException("The searializer is mandatory and "
                    + "mustn't be null");
        }
        this.serializer = serializer;
        this.mimeType = null;
    }

    /**
     * Creates a new {@link StringToByteArrayConverter} with the default 
     * {@link IDataUrlSerializer} and automatic {@link MimeType} guessing.
     * 
     * @param mimeType {@link MimeType} that is used while converting. Must not 
     * be {@code null}!
     * @throws NullPointerException If {@code mimeType} is {@code null}
     */
    public StringToByteArrayConverter(MimeType mimeType)
            throws NullPointerException {
        this(new DataUrlSerializer(), mimeType);
    }

    /**
     * Creates a new {@link StringToByteArrayConverter} with the default 
     * {@link IDataUrlSerializer} and automatic {@link MimeType} guessing.
     */
    public StringToByteArrayConverter() {
        this(new DataUrlSerializer());
    }

    @Override
    public byte[] convertToModel(String value,
            Class<? extends byte[]> targetType, Locale locale)
            throws ConversionException {
        if (value == null) {
            return null;
        }

        try {
            DataUrl dataUrl = serializer.unserialize(value);

            // If a MimeType was defined make sure that the data url has the
            // same MIME-Type
            if (mimeType != null && !matchMimeType(dataUrl, mimeType)) {
                throw new ConversionException("The MIME-Type of the given "
                        + "RFC 2397 data url String (" + dataUrl.getMimeType()
                        + ") doesn't match the required MimeType ("
                        + mimeType.getMimeType() + ")" );
            }

            return dataUrl.getData();
        } catch (MalformedURLException ex) {
            throw new ConversionException(ex);
        }
    }

    @Override
    public String convertToPresentation(byte[] value,
            Class<? extends String> targetType, Locale locale)
            throws ConversionException {
        if (value == null) {
            return null;
        }

        MimeType appliedMimeType;
        if (mimeType != null) {
            appliedMimeType = mimeType;
        } else {
            try {
                appliedMimeType = guessMimeType(value);
            } catch (IOException e) {
                throw new ConversionException(
                        "There was a problem with the stream", e);
            } catch (IllegalArgumentException e) {
                throw new ConversionException("Properly the MIME-Type guessing "
                        + "returned an unsupported MIME-Type", e);
            } catch (NullPointerException e) {
                throw new ConversionException("Properly the MIME-Type guessing "
                        + "failed and returned null", e);
            }
        }

        DataUrl dataUrl = new DataUrl(value, encoding,
                appliedMimeType.getMimeType());

        try {
            return serializer.serialize(dataUrl);
        } catch (MalformedURLException e) {
            throw new ConversionException(e);
        }
    }

    @Override
    public Class<byte[]> getModelType() {
        return byte[].class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

    /**
     * Guesses the {@link MimeType} of the given {@code byte[]} contents.
     *
     * @param data The image data.
     * @return The matching {@link MimeType}.
     * @see URLConnection#guessContentTypeFromStream(InputStream)
     * @throws IOException If something goes wrong with the data stream.
     * @throws IllegalArgumentException If the guessing resulted in an
     * unsupported MIME-Type.
     * @throws NullPointerException If the guessing did not find any matching
     * MIME-Type.
     */
    protected MimeType guessMimeType(byte[] data)
            throws IOException, IllegalArgumentException, NullPointerException {
        String mimeTypeString;
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(data);
            mimeTypeString = URLConnection.guessContentTypeFromStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return MimeType.valueOfMimeType(mimeTypeString);
    }

    /**
     * Matches the MIME-Type of the given {@code DataUrl} with the given
     * {@link MimeType}.
     *
     * @param dataUrl The {@link DataUrl} to match.
     * @param mimeType The {@link MimeType} to match against.
     * @return MIME-Type matches the {@link MimeType}.
     */
    protected boolean matchMimeType(DataUrl dataUrl, MimeType mimeType) {
        MimeType dataUrlMimeType;
        try {
            dataUrlMimeType = MimeType.valueOfMimeType(dataUrl.getMimeType());
        } catch (IllegalArgumentException e) {
            // The MIME-Type is not supported
            return false;
        }
        return mimeType.equals(dataUrlMimeType);
    }

}
