/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Competitor;
import com.example.models.CompetitorDTO;
//import com.example.models.Producto;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Typed;
import static javax.persistence.CascadeType.ALL;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONString;

/**
 *
 * @author Mauricio
 */
@Path("/competitors")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitorService {

    

    @PersistenceContext(unitName = "mongoPU")
     EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
        entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
                e.printStackTrace();
        }
    }
    
    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin","*").entity(competitors).build();
    }
    
    @GET
    @Path("/getall")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        Response response = Response.status(Response.Status.ACCEPTED).entity("Se a logeado al sistema").build();
        return response;
    }
    
    @GET
    @Path("/login/{correo}/{password}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@PathParam("correo") String correo,@PathParam("password") String password){
        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        Response response = Response.status(Response.Status.ACCEPTED).entity("Se a logeado al sistema").build();
        
        for (Competitor comp : competitors) {
            if(comp.getAddress().equals(correo)){
                if(comp.getPassword().equals(password)){
                    return response;
                }
            }
        }
        
       throw new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED).entity("No son las credenciales").build());        
    }
    
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
        public Response createCompetitor(CompetitorDTO competitor){
            
            Competitor c =new Competitor();
            JSONObject rta =new JSONObject();
            c.setAddress(competitor.getAddress());
            c.setAge(competitor.getAge());
            c.setCellphone(competitor.getCellphone());
            c.setCity(competitor.getCity());
            c.setCountry(competitor.getCountry());
            c.setName(competitor.getName());
            c.setSurname(competitor.getSurname());
            c.setTelephone(competitor.getTelephone());
            c.setVehicle(competitor.getVehicle());
            c.setProducto(competitor.getProducto());
            
            try{
                entityManager.getTransaction().begin();
                entityManager.persist(c);
                entityManager.getTransaction().commit();
                entityManager.refresh(c);
                rta.put("competitor_id", c.getId());
            }catch(Throwable t){
                t.printStackTrace();
                if(entityManager.getTransaction().isActive()){
                    entityManager.getTransaction().rollback();
                }
                c =null;
            }finally{
                entityManager.clear();
                entityManager.close();
            }
         return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toString()).build();   
        }
        
        @GET
        @Path("{name}")
        @Produces(MediaType.APPLICATION_JSON)
        public Response getCompetitorsByName(@PathParam("name")String name){
            TypedQuery<Competitor>query =(TypedQuery<Competitor>)
            entityManager.createQuery("SELECT c FROM Competitor c"+" WHERE c.name LIKE :name");
            List<Competitor>competitors =query.setParameter("name", name+"%").getResultList();
            return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();
        }
        
        

}
