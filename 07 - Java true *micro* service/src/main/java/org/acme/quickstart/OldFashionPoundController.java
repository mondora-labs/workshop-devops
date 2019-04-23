package org.acme.quickstart;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * OldFashionPoundController
 */
@Path("/oldFaschionPound")
public class OldFashionPoundController {

    @Inject
    OldFashionPoundService oldFashionPoundService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public OldFashionPound index() {
        return new OldFashionPound((int) (Math.random() * 10), (int) (Math.random() * 20), (int) (Math.random() * 12));
    }

    @POST
    @Path("/sum")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public OldFashionPound sum(SumRequestBody request) {
        return oldFashionPoundService.sum(request.amount1, request.amount2);
    }

}
