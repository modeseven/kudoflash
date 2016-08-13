package com.flash.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.flash.domain.Kudo;
import com.flash.repository.KudoRepository;
import com.flash.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Kudo.
 */
@RestController
@RequestMapping("/api")
public class KudoResource {

    private final Logger log = LoggerFactory.getLogger(KudoResource.class);
        
    @Inject
    private KudoRepository kudoRepository;
    
    /**
     * POST  /kudos : Create a new kudo.
     *
     * @param kudo the kudo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new kudo, or with status 400 (Bad Request) if the kudo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/kudos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Kudo> createKudo(@Valid @RequestBody Kudo kudo) throws URISyntaxException {
        log.debug("REST request to save Kudo : {}", kudo);
        if (kudo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("kudo", "idexists", "A new kudo cannot already have an ID")).body(null);
        }
        Kudo result = kudoRepository.save(kudo);
        return ResponseEntity.created(new URI("/api/kudos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("kudo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /kudos : Updates an existing kudo.
     *
     * @param kudo the kudo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated kudo,
     * or with status 400 (Bad Request) if the kudo is not valid,
     * or with status 500 (Internal Server Error) if the kudo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/kudos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Kudo> updateKudo(@Valid @RequestBody Kudo kudo) throws URISyntaxException {
        log.debug("REST request to update Kudo : {}", kudo);
        if (kudo.getId() == null) {
            return createKudo(kudo);
        }
        Kudo result = kudoRepository.save(kudo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("kudo", kudo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /kudos : get all the kudos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of kudos in body
     */
    @RequestMapping(value = "/kudos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Kudo> getAllKudos() {
        log.debug("REST request to get all Kudos");
        List<Kudo> kudos = kudoRepository.findAll();
        return kudos;
    }

    /**
     * GET  /kudos/:id : get the "id" kudo.
     *
     * @param id the id of the kudo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the kudo, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/kudos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Kudo> getKudo(@PathVariable Long id) {
        log.debug("REST request to get Kudo : {}", id);
        Kudo kudo = kudoRepository.findOne(id);
        return Optional.ofNullable(kudo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /kudos/:id : delete the "id" kudo.
     *
     * @param id the id of the kudo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/kudos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteKudo(@PathVariable Long id) {
        log.debug("REST request to delete Kudo : {}", id);
        kudoRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("kudo", id.toString())).build();
    }

}
