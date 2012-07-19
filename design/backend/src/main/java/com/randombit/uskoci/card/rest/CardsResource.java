package com.randombit.uskoci.card.rest;

import com.randombit.uskoci.card.dao.CardDAO;
import com.randombit.uskoci.card.model.Card;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
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
        cards.addAll(CardDAO.instance.getModel().values());
        return cards;
    }


    // returns the number of cards
    // Use http://localhost:8080/uskoci-REST/rest/cards/count
    // to get the total number of records
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        int count = CardDAO.instance.getModel().size();
        return String.valueOf(count);
    }

/*
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newCard(@FormParam("id") String id,
                        @FormParam("summary") String summary,
                        @FormParam("description") String description,
                        @FormParam("type") String type,
                        @Context HttpServletResponse servletResponse) throws IOException {

        Card card = new Card(id, summary, description, type);
        if (description!=null){
            card.setDescription(description);
        }
        CardDAO.instance.getModel().put(id, card);

        servletResponse.sendRedirect("../create_card.html");
    }
*/

    // Defines that the next path parameter after cards is
    // treated as a parameter and passed to the CardResources
    // Allows to type http://localhost:8080/uskoci-REST/rest/cards/1
    // 1 will be treated as parameter card and passed to CardResource
    @Path("{card}")
    public CardResource getCard(@PathParam("card") String id) {
        return new CardResource(uriInfo, request, id);
    }
}