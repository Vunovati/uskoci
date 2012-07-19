package com.randombit.uskoci.card.rest;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.model.Card;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBElement;

@Path("/card")
public class CardResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    String id;

    public CardResource(UriInfo uriInfo, Request request, String id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }

    //Application integration
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Card getCard() {
        Card card = CardDAO.instance.getModel().get(id);
        if(card==null)
            throw new RuntimeException("Get: Card with " + id +  " not found");
        return card;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putCard(JAXBElement<Card> card) {
        Card c = card.getValue();
        return putAndGetResponse(c);
    }

    @DELETE
    public void deleteCard() {
        Card c = CardDAO.instance.getModel().remove(id);
        if(c==null)
            throw new RuntimeException("Delete: Card with " + id +  " not found");
    }

    private Response putAndGetResponse(Card card) {
        Response res;
        if(CardDAO.instance.getModel().containsKey(card.getId())) {
            res = Response.noContent().build();
        } else {
            res = Response.created(uriInfo.getAbsolutePath()).build();
        }
        CardDAO.instance.getModel().put(card.getId(), card);
        return res;
    }
}