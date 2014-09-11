package eu.maxschuster.vaadin.signaturefield.dataurl.converter;

import java.net.MalformedURLException;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

import eu.maxschuster.vaadin.signaturefield.dataurl.DataURL;

public class StringToDataURLConverter implements Converter<String, DataURL> {

	@Override
	public DataURL convertToModel(String value,
			Class<? extends DataURL> targetType, Locale locale)
			throws ConversionException {
		try {
			return value == null ? null : new DataURL(value);
		} catch (MalformedURLException e) {
			throw new ConversionException(e);
		}
	}

	@Override
	public String convertToPresentation(DataURL value,
			Class<? extends String> targetType, Locale locale)
			throws ConversionException {
		return value == null ? null : value.toDataURLString();
	}

	@Override
	public Class<DataURL> getModelType() {
		return DataURL.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
