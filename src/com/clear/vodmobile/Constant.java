/**
 * Created on 2016-3-14
 *
 * @author: wgq
 */
package com.clear.vodmobile;

public class Constant {
	public enum ErrorCode {
		SERV_CONN_FAIL(1, "Can not connect to server"),
		SERV_INFO_PARSE_FAIL(2, "Failed to parse server info");
		
		ErrorCode(int ec, String es){
			errCode = ec;
			errStr = es;
		}
		
		public int getErrorCode() {
			return errCode;
		}
		
		public String getErrorString() {
			return errStr;
		}
		
		private int errCode;
		private String errStr;
	};
	
}
