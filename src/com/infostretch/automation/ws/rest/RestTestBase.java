/*******************************************************************************
 * QMetry Automation Framework provides a powerful and versatile platform to author 
 * Automated Test Cases in Behavior Driven, Keyword Driven or Code Driven approach
 *                
 * Copyright 2016 Infostretch Corporation
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
 * OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE
 *
 * You should have received a copy of the GNU General Public License along with this program in the name of LICENSE.txt in the root folder of the distribution. If not, see https://opensource.org/licenses/gpl-3.0.html
 *
 * See the NOTICE.TXT file in root folder of this source files distribution 
 * for additional information regarding copyright ownership and licenses
 * of other open source software / files used by QMetry Automation Framework.
 *
 * For any inquiry or need additional information, please contact support-qaf@infostretch.com
 *******************************************************************************/


package com.infostretch.automation.ws.rest;

import static com.infostretch.automation.core.ConfigurationManager.getBundle;

import java.util.Iterator;

import org.apache.commons.configuration.Configuration;

import com.infostretch.automation.core.QAFTestBase;
import com.infostretch.automation.core.TestBaseProvider;
import com.infostretch.automation.keys.ApplicationProperties;
import com.infostretch.automation.ws.Response;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ConnectionListenerFilter;

/**
 * com.infostretch.automation.ws.rest.RestTestBase.java
 * 
 * @author chirag
 */
public class RestTestBase {

	private static final String REST_CLIENT_KEY = "rest.client";
	private static final String REST_REQ_LOGGER_KEY = "rest.client.requestlogger";
	private static final String REST_REQ_TRACKER_KEY = "rest.client.requesttracker";
	private static final String REST_CLIENT_PROP_PREFIX = "com.sun.jersey.client.property";

	public Client getClient() {
		if (null == getTestBase().getContext().getObject(REST_CLIENT_KEY)) {
			setClient(Client.create());
		}
		return (Client) getTestBase().getContext().getObject(REST_CLIENT_KEY);
	}

	public void setClient(Client client) {
		client.addFilter(new ConnectionListenerFilter(getRequestListener()));
		client.addFilter(getRequestListener());
		client.addFilter(getRequestTracker());

		Configuration props = getBundle().subset(REST_CLIENT_PROP_PREFIX);
		Iterator<?> iter = props.getKeys();

		while (iter.hasNext()) {
			String prop = (String) iter.next();
			client.getProperties().put(REST_CLIENT_PROP_PREFIX + prop, props.getString(prop));
		}

		getTestBase().getContext().setProperty(REST_CLIENT_KEY, client);
	}

	public Response getResponse() {
		return new Response(getRequestTracker().getClientResponse());
	}

	public WebResource getWebResource(String resouce) {
		return getWebResource(ApplicationProperties.SELENIUM_BASE_URL.getStringVal(), resouce);
	}

	public WebResource getWebResource(String serviceEndPoint, String resouce) {
		return getClient().resource(serviceEndPoint).path(resouce);
	}

	public void resetClient() {
		getTestBase().getContext().clearProperty(REST_CLIENT_KEY);
	}

	private QAFTestBase getTestBase() {
		return TestBaseProvider.instance().get();
	}

	private RequestLogger getRequestListener() {
		RequestLogger requestLogger = (RequestLogger) getTestBase().getContext().getObject(REST_REQ_LOGGER_KEY);

		if (requestLogger == null) {
			requestLogger = new RequestLogger(System.out);
			getTestBase().getContext().setProperty(REST_REQ_LOGGER_KEY, requestLogger);
		}

		return requestLogger;
	}

	private RequestTracker getRequestTracker() {
		RequestTracker requestTracker = (RequestTracker) getTestBase().getContext().getObject(REST_REQ_TRACKER_KEY);
		if (requestTracker == null) {
			requestTracker = new RequestTracker();

			getTestBase().getContext().setProperty(REST_REQ_TRACKER_KEY, requestTracker);
		}

		return requestTracker;
	}

}
