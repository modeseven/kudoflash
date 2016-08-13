package com.flash.web.rest;

import com.flash.KudoflashApp;
import com.flash.domain.Kudo;
import com.flash.repository.KudoRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the KudoResource REST controller.
 *
 * @see KudoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = KudoflashApp.class)
@WebAppConfiguration
@IntegrationTest
public class KudoResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_TEXT = "AAAAA";
    private static final String UPDATED_TEXT = "BBBBB";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_STR = dateTimeFormatter.format(DEFAULT_DATE);

    @Inject
    private KudoRepository kudoRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restKudoMockMvc;

    private Kudo kudo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        KudoResource kudoResource = new KudoResource();
        ReflectionTestUtils.setField(kudoResource, "kudoRepository", kudoRepository);
        this.restKudoMockMvc = MockMvcBuilders.standaloneSetup(kudoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        kudo = new Kudo();
        kudo.setText(DEFAULT_TEXT);
        kudo.setDate(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void createKudo() throws Exception {
        int databaseSizeBeforeCreate = kudoRepository.findAll().size();

        // Create the Kudo

        restKudoMockMvc.perform(post("/api/kudos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(kudo)))
                .andExpect(status().isCreated());

        // Validate the Kudo in the database
        List<Kudo> kudos = kudoRepository.findAll();
        assertThat(kudos).hasSize(databaseSizeBeforeCreate + 1);
        Kudo testKudo = kudos.get(kudos.size() - 1);
        assertThat(testKudo.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testKudo.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    public void checkTextIsRequired() throws Exception {
        int databaseSizeBeforeTest = kudoRepository.findAll().size();
        // set the field null
        kudo.setText(null);

        // Create the Kudo, which fails.

        restKudoMockMvc.perform(post("/api/kudos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(kudo)))
                .andExpect(status().isBadRequest());

        List<Kudo> kudos = kudoRepository.findAll();
        assertThat(kudos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = kudoRepository.findAll().size();
        // set the field null
        kudo.setDate(null);

        // Create the Kudo, which fails.

        restKudoMockMvc.perform(post("/api/kudos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(kudo)))
                .andExpect(status().isBadRequest());

        List<Kudo> kudos = kudoRepository.findAll();
        assertThat(kudos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllKudos() throws Exception {
        // Initialize the database
        kudoRepository.saveAndFlush(kudo);

        // Get all the kudos
        restKudoMockMvc.perform(get("/api/kudos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(kudo.getId().intValue())))
                .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE_STR)));
    }

    @Test
    @Transactional
    public void getKudo() throws Exception {
        // Initialize the database
        kudoRepository.saveAndFlush(kudo);

        // Get the kudo
        restKudoMockMvc.perform(get("/api/kudos/{id}", kudo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(kudo.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingKudo() throws Exception {
        // Get the kudo
        restKudoMockMvc.perform(get("/api/kudos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateKudo() throws Exception {
        // Initialize the database
        kudoRepository.saveAndFlush(kudo);
        int databaseSizeBeforeUpdate = kudoRepository.findAll().size();

        // Update the kudo
        Kudo updatedKudo = new Kudo();
        updatedKudo.setId(kudo.getId());
        updatedKudo.setText(UPDATED_TEXT);
        updatedKudo.setDate(UPDATED_DATE);

        restKudoMockMvc.perform(put("/api/kudos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedKudo)))
                .andExpect(status().isOk());

        // Validate the Kudo in the database
        List<Kudo> kudos = kudoRepository.findAll();
        assertThat(kudos).hasSize(databaseSizeBeforeUpdate);
        Kudo testKudo = kudos.get(kudos.size() - 1);
        assertThat(testKudo.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testKudo.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    public void deleteKudo() throws Exception {
        // Initialize the database
        kudoRepository.saveAndFlush(kudo);
        int databaseSizeBeforeDelete = kudoRepository.findAll().size();

        // Get the kudo
        restKudoMockMvc.perform(delete("/api/kudos/{id}", kudo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Kudo> kudos = kudoRepository.findAll();
        assertThat(kudos).hasSize(databaseSizeBeforeDelete - 1);
    }
}
