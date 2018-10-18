/*
 * Copyright 2016-2017 Shanghai Boyuan IT Services Ltd.
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

package io.microvibe.booster.pay.alipay.bean;

import lombok.Data;

/**
 * 支付宝对商户的请求数据处理完成后，会将处理的结果数据通过系统程序控制客户端页面自动跳转的方式通知给商户网站。这些处理结果数据就是页面跳转同步通知参数。
 *
 * @author Qt
 * @see <a href="https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.7EWBns&treeId=62&articleId=104743&docType=1#s1">https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.7EWBns&treeId=62&articleId=104743&docType=1#s1</a>
 */
@Data
public class AsyncNotify {

	private String sign_type;// 签名方式  DSA, RSA, MD5.
	private String sign;// 签名

	private String notify_type; // 通知类型, value： trade_status_sync.
	private String notify_time; //通知时间（支付宝时间），格式：YYYY-MM-DD hh:mm:ss
	private String notify_id; // 支付宝通知流水号，境外商户可以用这个流水号询问支付宝该条通知的合法性

	private String trade_status; //交易状态: TRADE_FINISHED, TRADE_CLOSED

	private String trade_no;// 支付宝交易号。最短16位，最长64位	2015070800001000100080029361
	private String out_trade_no;//商户订单号/境外商户交易号（确保在境外商户系统中唯一）	2525759240575424
	private String currency;//结算币种.	USD
	private String total_fee;//商品的外币金额，范围是0.01～1000000.00.

	private String total_amount;//付款金额


	private String app_id;
	private String auth_app_id;
	private String buyer_id;
	private String buyer_pay_amount;
	private String charset;
	private String fund_bill_list;
	private String gmt_create;
	private String gmt_payment;
	private String invoice_amount;
	private String point_amount;
	private String receipt_amount;
	private String seller_id;
	private String subject;
	private String version;

}
