/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ObjectMapperFactory.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 *
 */
public class ObjectMapperFactory
{
	/**
	 * 初期処理（JSON 変換に関する各種設定）.
	 *
	 * @param om
	 * @return ObjectMapper
	 *
	 * @see "http://wiki.fasterxml.com/JacksonFeaturesDeserialization"
	 * @see "http://wiki.fasterxml.com/JacksonFeaturesSerialization"
	 */
	public static ObjectMapper setupObjectMapper( ObjectMapper om )
	{
		if ( om == null ) om = new ObjectMapper();

		// ----- シリアライズ（出力）設定

		om.setSerializationInclusion( JsonInclude.Include.NON_NULL );

		om.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
		om.configure( SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true );
		om.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
		om.configure( SerializationFeature.INDENT_OUTPUT, true );

		// ----- デシリアライズ（入力）設定

		om.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false ) ;
		om.configure( DeserializationFeature.READ_ENUMS_USING_TO_STRING, true ) ;

		om.setDateFormat( new DateFormatter( DATE_FORMAT ) ) ;
		om.getDateFormat().setLenient( false ) ;

//		SimpleModule module = new SimpleModule(
//			"CustomModule", new Version( 1, 0, 0, null, null, null ) );
//
//		module.addSerializer( xxx.class, new XxxSerializer() );
//		module.addDeserializer( xxx.class, new XxxDeserializer() );
//
//		om.registerModule( module );

		return om;
	}

	/** 日時の出力時に使用する書式 */
	private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

	/**
	 * 日付解析クラス
	 *
	 * @author itsukaha
	 */
	private static class DateFormatter extends SimpleDateFormat
	{
		/** serialVersionUID */
		private static final long serialVersionUID = 1L ;

		/**
		 * 解析（parse）時に許容する書式群
		 *
		 * マッチする書式がみつかるまでループするので、
		 * 利用頻度の高い順に並べておくこと
		 */
		private static final String[] spareFormat = new String[]
		{
			"yyyy-MM-dd'T'HH:mm:ss",
			"yyyy-MM-dd'T'HH:mm:ss.SSS",

			"yyyy/MM/dd HH:mm:ss",

			"yyyy/MM/dd HH:mm:ssZ",
			"yyyy/MM/dd HH:mm:ss.SSSZ",
			"yyyy/MM/dd HH:mm:ss.SSS",

			"yyyy-MM-dd'T'HH:mm:ssZ",
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ",
		} ;

		/**
		 * コンストラクタ.
		 *
		 * @param formats デフォルト書式
		 */
		public DateFormatter( String formats )
		{
			super( formats ) ;
		}

		/**
		 * 文字列解析
		 *
		 * @param str 解析する文字列
		 * @return 解析結果の Date オブジェクト
		 *
		 * @throws ParseException 解析エラー
		 */
		@Override
		public Date parse( String str ) throws ParseException
		{
			ParseException firstException = null ;

			// 書式候補を順に試して一致するものを探す
			// 一致するものがなかったら、最初に発生した例外をスローする

			for ( String format : spareFormat )
			{
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat( format ) ;
					sdf.setLenient( false );

					Date ret = sdf.parse( str ) ;
					if ( str.equals( sdf.format( ret ) ) )
					{
						return ret;
					}
				}
				catch ( ParseException ex ) // 次を試す
				{
					if( firstException == null ) firstException = ex ;
				}
			}

			// 一致するものがない かつ 例外も発生していない
			// →余分な情報がついている文字列

			if ( firstException == null )
			{
				firstException = new ParseException(
					"'" + str + "' is not able to parse as date/time.", -1 );
			}

			throw firstException ;
		}
	}
}
