package eu.vital.reply.clients;

import eu.vital.reply.utils.HttpCommonClient;
import eu.vital.reply.utils.JsonUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import eu.vital.reply.jsonpojos.CityBikesNetwork;
import eu.vital.reply.jsonpojos.CityBikesNetworks;

public class IoTSystemClient
{
    private HttpCommonClient httpCC;

    public IoTSystemClient() {
        httpCC = HttpCommonClient.getInstance();
    }
    
    private String performRequest(URI uri) throws ClientProtocolException, IOException {
    	String response = null;
    	int code;

    	HttpGet get = new HttpGet(uri);
    	get.setConfig(RequestConfig.custom().setConnectionRequestTimeout(3000).setConnectTimeout(3000).setSocketTimeout(3000).build());

        CloseableHttpResponse resp;
        try {
            resp = httpCC.httpc.execute(get);
            code = resp.getStatusLine().getStatusCode();
            if(code >= 200 && code <= 299) {
            	response = EntityUtils.toString(resp.getEntity());
            }
            resp.close();
        } catch (Exception e) {
            try {
            	try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
            	get.setConfig(RequestConfig.custom().setConnectionRequestTimeout(7000).setConnectTimeout(7000).setSocketTimeout(7000).build());
                resp = httpCC.httpc.execute(get);
                code = resp.getStatusLine().getStatusCode();
                if(code >= 200 && code <= 299) {
                	response = EntityUtils.toString(resp.getEntity());
                }
                resp.close();
            } catch (IOException ea) {
            	try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
            	get.setConfig(RequestConfig.custom().setConnectionRequestTimeout(12000).setConnectTimeout(12000).setSocketTimeout(12000).build());
                resp = httpCC.httpc.execute(get);
                code = resp.getStatusLine().getStatusCode();
                if(code >= 200 && code <= 299) {
                	response = EntityUtils.toString(resp.getEntity());
                }
                resp.close();
            }
        }

    	return response;
    }
    
    public CityBikesNetwork getNetwork(String apiBasePath, String networkId) {
    	URI uri;
    	String respString = null;
    	CityBikesNetwork network = null;

    	try {
			uri = new URI(apiBasePath + "/" + networkId);
			try {
				respString = performRequest(uri);
			} catch (IOException e) {
				e.printStackTrace();
			}
	        if(respString != null) {
	        	try {
					network = (CityBikesNetwork) JsonUtils.deserializeJson(respString, CityBikesNetwork.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

        return network;
    }

    public CityBikesNetworks getNetworks(String apiBasePath) {
    	URI uri;
    	String respString = null;
    	CityBikesNetworks networks = null;

    	try {
			uri = new URI(apiBasePath);
			try {
				respString = performRequest(uri);
			} catch (IOException e) {
				e.printStackTrace();
			}
	        if(respString != null) {
	        	try {
					networks = (CityBikesNetworks) JsonUtils.deserializeJson(respString, CityBikesNetworks.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

        return networks;
    }
}
