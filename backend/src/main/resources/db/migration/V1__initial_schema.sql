--
-- PostgreSQL database dump
--

-- Dumped from database version 18.6 (Debian 18.6-1.pgdg13+2)
-- Dumped by pg_dump version 18.6 (Debian 18.6-1.pgdg13+2)

--
-- Name: heroes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.heroes (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    element character varying(255),
    hero_class character varying(255),
    name character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone,
    CONSTRAINT heroes_element_check CHECK (((element)::text = ANY ((ARRAY['FIRE'::character varying, 'ICE'::character varying, 'EARTH'::character varying, 'LIGHT'::character varying, 'DARK'::character varying])::text[]))),
    CONSTRAINT heroes_hero_class_check CHECK (((hero_class)::text = ANY ((ARRAY['WARRIOR'::character varying, 'KNIGHT'::character varying, 'RANGER'::character varying, 'MAGE'::character varying, 'SOULWEAVER'::character varying, 'THIEF'::character varying])::text[])))
);


--
-- Name: match_heroes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.match_heroes (
    id character varying(255) NOT NULL,
    banned boolean NOT NULL,
    created_at timestamp(6) without time zone,
    pick_order integer NOT NULL,
    team integer NOT NULL,
    updated_at timestamp(6) without time zone,
    hero_id character varying(255) NOT NULL,
    match_id character varying(255) NOT NULL
);


--
-- Name: match_prebans; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.match_prebans (
    match_id character varying(255) NOT NULL,
    hero_id character varying(255) NOT NULL
);


--
-- Name: matches; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.matches (
    id character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    fp_win boolean NOT NULL,
    updated_at timestamp(6) without time zone,
    user_team integer NOT NULL
);


--
-- Name: heroes heroes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.heroes
    ADD CONSTRAINT heroes_pkey PRIMARY KEY (id);


--
-- Name: match_heroes match_heroes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.match_heroes
    ADD CONSTRAINT match_heroes_pkey PRIMARY KEY (id);


--
-- Name: matches matches_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.matches
    ADD CONSTRAINT matches_pkey PRIMARY KEY (id);


--
-- Name: match_heroes fk42mclahjcrin6skevd3e0q2gh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.match_heroes
    ADD CONSTRAINT fk42mclahjcrin6skevd3e0q2gh FOREIGN KEY (hero_id) REFERENCES public.heroes(id);


--
-- Name: match_heroes fkkunexibxlv49jt886weaydk6s; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.match_heroes
    ADD CONSTRAINT fkkunexibxlv49jt886weaydk6s FOREIGN KEY (match_id) REFERENCES public.matches(id);


--
-- Name: match_prebans fkms9qof3alnm5l01egs369cx9s; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.match_prebans
    ADD CONSTRAINT fkms9qof3alnm5l01egs369cx9s FOREIGN KEY (hero_id) REFERENCES public.heroes(id);


--
-- Name: match_prebans fkoq8119lanhgymqfbkmlfedt7s; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.match_prebans
    ADD CONSTRAINT fkoq8119lanhgymqfbkmlfedt7s FOREIGN KEY (match_id) REFERENCES public.matches(id);


--
-- PostgreSQL database dump complete
--

