package com.example.socialcloud.Operation;

import com.example.socialcloud.Util.Global;
import com.example.socialcloud.Model.ServiceName;

/**
 * Format class to get the Public Keys for a specific service
 */
public class GetPK extends FormatGenerator {

    ServiceName sn;

    /**
     * Constructor for URL and JSON, here i set the service i want to get the public keys of
     * @param service name of the service
     */
    public GetPK(ServiceName service){
        super();
        url = makeStaticURL(Global.getGetPK_path());

        sn = service;
        addService(sn.toString());
    }

    /**
     * Void constructor for getting the general URL of the request
     */
    public GetPK(){
        super();
        url = makeStaticURL(Global.getGetPK_path());
    }

    /**
     * Setter of the service
     * @param service name of the service
     */
    private void addService(String service) {
        addObjectToJSON("name", service);
    }
}
