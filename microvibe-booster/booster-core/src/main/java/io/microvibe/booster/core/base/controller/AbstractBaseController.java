package io.microvibe.booster.core.base.controller;

import com.sun.beans.editors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CharacterEditor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public abstract class AbstractBaseController {
	@Autowired
	protected ApplicationContext context;
	@Autowired
	protected Environment environment;
	@Autowired
	protected MessageSource messageSource;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {

		// prime
		binder.registerCustomEditor(boolean.class,new CustomBooleanEditor(true));
		binder.registerCustomEditor(char.class,new CharacterEditor(true));
		binder.registerCustomEditor(int.class,new IntegerEditor(){
			@Override
			public void setAsText(String s) throws IllegalArgumentException {
				try {
					super.setAsText(s);
				} catch (NumberFormatException e) {
					log.warn(e.getMessage(), e);
					setValue(Integer.valueOf(0).intValue());
				}
			}
		});
		binder.registerCustomEditor(long.class,new LongEditor(){
			@Override
			public void setAsText(String s) throws IllegalArgumentException {
				try {
					super.setAsText(s);
				} catch (NumberFormatException e) {
					log.warn(e.getMessage(), e);
					setValue(Long.valueOf(0L).longValue());
				}
			}
		});
		binder.registerCustomEditor(double.class,new DoubleEditor(){
			@Override
			public void setAsText(String s) throws IllegalArgumentException {
				try {
					super.setAsText(s);
				} catch (NumberFormatException e) {
					log.warn(e.getMessage(), e);
					setValue(Double.valueOf(0.0D).doubleValue());
				}
			}
		});
		binder.registerCustomEditor(float.class,new FloatEditor(){
			@Override
			public void setAsText(String s) throws IllegalArgumentException {
				try {
					super.setAsText(s);
				} catch (NumberFormatException e) {
					log.warn(e.getMessage(), e);
					setValue(Float.valueOf(0.0F).floatValue());
				}
			}
		});
		binder.registerCustomEditor(byte.class,new ByteEditor(){
			@Override
			public void setAsText(String s) throws IllegalArgumentException {
				try {
					super.setAsText(s);
				} catch (NumberFormatException e) {
					log.warn(e.getMessage(), e);
					setValue(Byte.valueOf((byte)0).byteValue());
				}
			}
		});
		binder.registerCustomEditor(short.class,new ShortEditor(){
			@Override
			public void setAsText(String s) throws IllegalArgumentException {
				try {
					super.setAsText(s);
				} catch (NumberFormatException e) {
					log.warn(e.getMessage(), e);
					setValue(Short.valueOf((short)0).shortValue());
				}
			}
		});
		// date
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
		/*
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
			new SimpleDateFormat("yyyy-MM-dd"), true));

		// timestamp
		binder.registerCustomEditor(Timestamp.class, new CustomDateEditor(
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
		binder.registerCustomEditor(Timestamp.class, new CustomDateEditor(
			new SimpleDateFormat("yyyy-MM-dd"), true));
		*/

	}
}
