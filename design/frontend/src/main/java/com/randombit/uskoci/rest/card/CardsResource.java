package com.randombit.uskoci.rest.card;

import com.randombit.uskoci.card.dao.SingletonCardDB;
import com.randombit.uskoci.card.model.Card;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;


// Will map the resource to the URL cards
@Path("/cards")
public class CardsResource {
    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;


    // Return the list of cards for applications
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Card> getCards() {
        List<Card> cards = new ArrayList<Card>();
        cards.addAll(SingletonCardDB.instance.getModel().values());
        return cards;
    }


    // returns the number of cards
    // Use http://localhost:8080/uskoci-REST/rest/cards/count
    // to get the total number of records
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        int count = SingletonCardDB.instance.getModel().size();
        return String.valueOf(count);
    }

    @POST
    @Path("add")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    public void newCard(Card card) {

        String result = "Card saved :" + card;

        SingletonCardDB.instance.getModel().put(card.getId(), card);

        Response.status(201).entity(result).build();
    }
}