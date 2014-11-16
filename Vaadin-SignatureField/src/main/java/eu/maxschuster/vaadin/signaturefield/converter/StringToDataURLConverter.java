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
package eu.maxschuster.vaadin.signaturefield.converter;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlSerializer;
import eu.maxschuster.dataurl.DefaultDataUrlSerializer;
import java.net.MalformedURLException;

/**
 * A converter that converts from {@link String} to {@link DataUrl} and back.
 *
 * @author Max Schuster
 */
public class StringToDataURLConverter implements Converter<String, DataUrl> {

    private final DataUrlSerializer serializer;

    public StringToDataURLConverter(DataUrlSerializer serializer) {
        this.serializer = serializer;
    }

    public StringToDataURLConverter() {
        this(DefaultDataUrlSerializer.get());
    }

    @Override
    public DataUrl convertToModel(String value,
            Class<? extends DataUrl> targetType, Locale locale)
            throws ConversionException {
        try {
            return value == null ? null : serializer.unserialize(value);
        } catch (MalformedURLException e) {
            throw new ConversionException(e);
        }
    }

    @Override
    public String convertToPresentation(DataUrl value,
            Class<? extends String> targetType, Locale locale)
            throws ConversionException {
        try {
            return value == null ? null : serializer.serialize(value);
        } catch (MalformedURLException e) {
            throw new ConversionException(e);
        }
    }

    @Override
    public Class<DataUrl> getModelType() {
        return DataUrl.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

    public DataUrlSerializer getSerializer() {
        return serializer;
    }

}
