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

package eu.maxschuster.vaadin.signaturefield.shared;

import eu.maxschuster.vaadin.signaturefield.SignatureField;

/**
 * Predefined MIME-Types for the {@link SignatureField}
 * 
 * @author Max Schuster <dev@maxschuster.eu>
 */
public enum MimeType {

	PNG("image/png"),
	JPEG("image/jpeg");
	
	private final String mimeType;
	
	MimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * @return MIME-Type as {@link String}
	 */
	public String getMimeType() {
		return mimeType;
	}
	
	/**
	 * Searches the matching {@link MimeType} instance for
	 * the given MIME-Type {@link String}
	 * 
	 * @param mimeType MIME-Type as {@link String}
	 * @return Matching {@link MimeType}
	 * @throws NullPointerException
	 * If mimeType is <code>null</code>
	 * @throws IllegalArgumentException
	 * If no matching {@link MimeType} was found
	 */
	public static MimeType valueOfMimeType(String mimeType) throws
			NullPointerException, IllegalArgumentException {
		if (mimeType == null) {
			throw new NullPointerException("mimeType must not be null!");
		}
		for (MimeType mime : values()) {
			if (mime.getMimeType().equals(mimeType)) {
				return mime;
			}
		}
		throw new IllegalArgumentException("MIME-Type " + mimeType + " not found!");
	}
	
}
