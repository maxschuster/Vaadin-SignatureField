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

package eu.maxschuster.vaadin.signaturefield;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

/**
 * Creates and parses data urls.
 * 
 * @author Max Schuster <dev@maxschuster.eu>
 * @see http://en.wikipedia.org/wiki/Data_URI_scheme
 */
public class DataURL implements Serializable {
	// TODO Test this class more
	
	private static final long serialVersionUID = 1L;
	
	public static final String CHARSET_UTF_8 = "UTF-8";
	
	public static final String CHARSET_ASCII = "US-ASCII";
	
	
	public static final Encoder ENCODER_URL = new URLEncodedEncoder();
	
	public static final Encoder ENCODER_BASE64 = new Base64Encoder();
	
	
	public static final String DEFAULT_CHARSET = CHARSET_ASCII;
	
	public static final Encoder DEFALUT_ENCODER = ENCODER_URL;
	
	public static final String DEFAULT_MIMETYPE = "text/plain";
	
	
	private static final String PROTOCOL = "data:";
	
	private static final Pattern PATTERN_META_SPLIT =
			Pattern.compile(";");
	
	private static final Pattern PATTERN_MIMETYPE =
			Pattern.compile("^[a-z\\-0-9]+\\/[a-z\\-0-9	]+$");
	
	private static final String HEADER_CHARSET = "charset";

	/**
	 * Payload of this data url
	 */
	private byte[] data;
	
	/**
	 * MIME-Type of this data urls content
	 */
	private String mimeType;
	
	/**
	 * Headers/parameters of this data url
	 */
	private final Map<String, String> headers = new HashMap<String, String>();
	
	/**
	 * Encoder used to encode/decode the payload of this data url
	 */
	private Encoder encoder;
	
	/**
	 * Creates new empty data url.
	 */
	public DataURL() { }
	
	/**
	 * Creates new data url from the given data url string
	 * 
	 * @param dataUrlString Data url string to parse
	 * @throws MalformedURLException
	 * @throws NullPointerException If dataUrlString is <code>null</code>
	 */
	public DataURL(String dataUrlString) throws MalformedURLException, NullPointerException {
		parse(dataUrlString);
	}
	
	/**
	 * Creates new data url from the given parameters
	 * 
	 * @param mimeType MIME-Type of this data urls content
	 * @param headers Headers/parameters of this data url
	 * @param encoder Encoder used to encode/decode the payload of this data url
	 * @param data Payload of this data url
	 */
	public DataURL(String mimeType, Map<String, String> headers,
			Encoder encoder, byte[] data) {
		super();
		this.mimeType = mimeType;
		replaceHeaders(headers);
		this.encoder = encoder;
		this.data = data;
	}

	/**
	 * Parses dataUrlString and stores it inside this {@link DataURL} instance
	 * 
	 * @param dataUrlString Data url string to parse
	 * @return This {@link DataURL} instance
	 * @throws MalformedURLException If dataUrlString can't be parsed
	 * @throws NullPointerException If dataUrlString is <code>null</code>
	 */
	public DataURL parse(String dataUrlString) throws MalformedURLException, NullPointerException {
		
		if (dataUrlString == null) {
			throw new NullPointerException();
		}	
		
		byte[] data = null;
		String mimeType = null;
		HashMap<String, String> headers = new HashMap<String, String>();
		Encoder encoder = DEFALUT_ENCODER;
		
		if (!dataUrlString.startsWith(PROTOCOL)) {
			throw new MalformedURLException("Wrong protocol");
		}
		
		int colon = dataUrlString.indexOf(':');
		int comma = dataUrlString.indexOf(',');
		
		String metaString = dataUrlString.substring(colon + 1, comma);
		String dataString = dataUrlString.substring(comma + 1);
		
		String[] metaArray = PATTERN_META_SPLIT.split(metaString);
		for (int i = 0; i < metaArray.length; i++) {
			String meta = metaArray[i];
			if (i == 0) {
				Matcher m = PATTERN_MIMETYPE.matcher(meta);
				if (m.matches()) {
					mimeType = meta;
					continue;
				}
			}
			
			if (i + 1 == metaArray.length) {
				if (meta.equals(Base64Encoder.NAME)) {
					encoder = Base64Encoder.INSTANCE;
					continue;
				}
				String charset = headers.get(HEADER_CHARSET);
				if (charset == null) {
					charset = DEFAULT_CHARSET;
				}
				encoder = new URLEncodedEncoder();
			}
			
			int equals = meta.indexOf('=');
			if (equals < 1) {
				throw new MalformedURLException();
			}
			
			String name = meta.substring(0, equals);
			String value = meta.substring(equals + 1);
			
			try {
				headers.put(name, URLEncoding.decode(value, CHARSET_UTF_8));
			} catch (UnsupportedEncodingException e) {
				throw new AssertionError();
			}
		}
		
		try {
			data = encoder.decode(this, dataString);
		} catch (Exception e) {
			throw new MalformedURLException();
		}
		
		this.data = data;
		this.mimeType = mimeType;
		replaceHeaders(headers);
		this.encoder = encoder;
		
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + ((encoder == null) ? 0 : encoder.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result
				+ ((mimeType == null) ? 0 : mimeType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataURL other = (DataURL) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		if (encoder == null) {
			if (other.encoder != null)
				return false;
		} else if (!encoder.equals(other.encoder))
			return false;
		if (headers == null) {
			if (other.headers != null)
				return false;
		} else if (!headers.equals(other.headers))
			return false;
		if (mimeType == null) {
			if (other.mimeType != null)
				return false;
		} else if (!mimeType.equals(other.mimeType))
			return false;
		return true;
	}

	/**
	 * Creates a data url string from this {@link DataURL}
	 * 
	 * @return Data url string
	 * @throws MalformedURLException If the url string could not be created
	 */
	public String toDataURLString() throws MalformedURLException {
		Encoder encoder = getAppliedEncoder();
		String encoderName = encoder.getName();
		int headerSize = headers.size();
		StringBuilder sb = new StringBuilder(PROTOCOL);
		
		String mimeType = getAppliedMimeType();
		
		if (mimeType != null) {
			sb.append(mimeType);
			if (headerSize > 0 || encoderName != null) {
				sb.append(";");
			}
		}
		
		if (headerSize > 0) {
			int i = 0;
			for (Entry<String, String> entry : headers.entrySet()) {
				String value;
				try {
					value = URLEncoding.encode(entry.getValue(), CHARSET_UTF_8);
				} catch (UnsupportedEncodingException e) {
					throw new AssertionError();
				}
				sb.append(entry.getKey()).append('=').append(value);
				i++;
				if (i < headerSize || encoderName != null) {
					sb.append(';');
				}
			}
		}
		
		if (encoderName != null) {
			sb.append(encoderName);
		}
		
		sb.append(',');
		
		try {
			sb.append(encoder.encode(this, data));
		} catch (Exception e) {
			throw new MalformedURLException();
		}
		
		return sb.toString();
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Returns a {@link Map} of headers which can be changed
	 * @return {@link Map} of headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	/**
	 * Replaces the contents of the {@link DataURL#headers}
	 * map with the contents of the given {@link Map}. If the
	 * given {@link Map} is <code>null</code> the
	 * {@link DataURL#headers} map is only cleared
	 * 
	 * @param headers Headers to replace the existing ones
	 */
	protected void replaceHeaders(Map<String, String> headers) {
		this.headers.clear();
		if (headers != null) {
			this.headers.putAll(headers);
		}
	}

	public Encoder getEncoder() {
		return encoder;
	}

	public void setEncoder(Encoder encoder) {
		this.encoder = encoder;
	}
	
	public String getCharset() {
		return headers.get(HEADER_CHARSET);
	}
	
	public void setCharset(String charset) {
		headers.put(HEADER_CHARSET, charset);
	}
	
	/**
	 * Returns the applied charset.
	 * Can't be <code>null</code>
	 * 
	 * @return Applied charset
	 */
	public String getAppliedCharset() {
		String charset = getCharset();
		return charset != null ? charset : DEFAULT_CHARSET;
	}
	
	/**
	 * Returns the applied MIME-Type.
	 * Can't be <code>null</code>
	 * 
	 * @return Applied MIME-Type
	 */
	public String getAppliedMimeType() {
		return mimeType != null ? mimeType : DEFAULT_MIMETYPE;
	}
	
	/**
	 * Returns the applied encoder.
	 * Can't be <code>null</code>
	 * 
	 * @return Applied encoder
	 */
	public Encoder getAppliedEncoder() {
		return encoder != null ? encoder : DEFALUT_ENCODER;
	}

	/**
	 * A encoder for {@link DataURL}s
	 * 
	 * @author Max
	 *
	 */
	public interface Encoder {
		
		/**
		 * Gets the name of the encoder.
		 * Should always return the same {@link String}
		 * or <code>null</code> in case of the default decoder
		 * 
		 * @return Name of the encoder
		 */
		public String getName();
		
		/**
		 * Decodes the given {@link String}
		 * 
		 * @param dataURL {@link DataURL} instance
		 * @param string String to decode
		 * @return Decoded data
		 * @throws Exception If something goes wrong
		 */
		public byte[] decode(DataURL dataURL, String string) throws Exception;
		
		/**
		 * Encodes the given byte[] of data
		 * @param dataURL {@link DataURL} instance
		 * @param data String to encode
		 * @return Encoded String
		 * @throws Exception
		 */
		public String encode(DataURL dataURL, byte[] data) throws Exception;
	
		@Override
		public int hashCode();

		@Override
		public boolean equals(Object obj);
		
	}
	
	/**
	 * Base64 implementation of {@link Encoder}
	 * 
	 * @author Max Schuster <dev@maxschuster.eu>
	 */
	public static class Base64Encoder implements Encoder {
		
		public static final String NAME = "base64";
		
		public static final Base64Encoder INSTANCE =
				new Base64Encoder();

		@Override
		public byte[] decode(DataURL dataURL, String string) {
			return DatatypeConverter.parseBase64Binary(string);
		}

		@Override
		public String encode(DataURL dataURL, byte[] data) {
			return DatatypeConverter.printBase64Binary(data);
		}

		@Override
		public final String getName() {
			return NAME;
		}

		@Override
		public int hashCode() {
			return Base64Encoder.class.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			return true;
		}
		
		
		
	}
	
	/**
	 * URL Encoded implemention of {@link Encoder}
	 * 
	 * @author Max Schuster <dev@maxschuster.eu>
	 */
	public static class URLEncodedEncoder implements Encoder {

		public URLEncodedEncoder() {
			super();
		}

		@Override
		public byte[] decode(DataURL dataURL, String string) throws Exception {
			String charset = dataURL.getAppliedCharset();
			return URLEncoding.decode(string, charset).getBytes(string);
		}

		@Override
		public String encode(DataURL dataURL, byte[] data) throws Exception {
			String charset = dataURL.getAppliedCharset();
			return URLEncoding.encode(new String(data, charset), charset);
		}

		@Override
		public final String getName() {
			return null;
		}
		
		@Override
		public int hashCode() {
			return URLEncodedEncoder.class.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			return true;
		}
		
	}
	
	/**
	 * Encodes/Decodes a {@link String} RFC 3986 compatible
	 * 
	 * @author Max Schuster <dev@maxschuster.eu>
	 */
	private static class URLEncoding {
		
		/**
		 * Encodes a {@link String}
		 * 
		 * @param s String to encode
		 * @param enc Encoding to use
		 * @return Encoded {@link String}
		 * @throws UnsupportedEncodingException If the named encoding is not supported 
		 */
		public static String encode(String s, String enc) throws UnsupportedEncodingException {
			return URLEncoder.encode(s, enc).replace("%20", "+");
		}
		
		/**
		 * Decodes a {@link String}
		 * 
		 * @param s String to decode
		 * @param enc Encoding to use
		 * @return Decoded {@link String}
		 * @throws UnsupportedEncodingException If the named encoding is not supported 
		 */
		public static String decode(String s, String enc) throws UnsupportedEncodingException {
			return URLDecoder.decode(s.replace("+", "%20"), enc);
		}
		
	}
	
}
