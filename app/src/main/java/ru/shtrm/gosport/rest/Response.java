package ru.shtrm.gosport.rest;

import java.util.List;
import java.util.Map;

public class Response {
	public int mStatus;
	public byte[] mBody;
	public Map<String, List<String>> mHeaders;

	/**
	 * 
	 */
	public Response(int status, Map<String, List<String>> headers, byte[] body) {
		mStatus = status;
		mHeaders = headers;
		mBody = body;
	}

}
