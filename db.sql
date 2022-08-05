--
-- PostgreSQL database dump
--

-- Dumped from database version 14.4
-- Dumped by pg_dump version 14.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: state; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.state AS ENUM (
    'opening',
    'closed',
    'processing'
);


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: guild; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.guild (
    id bigint NOT NULL,
    container bigint NOT NULL,
    manager_role bigint
);


--
-- Name: request; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.request (
    id integer NOT NULL,
    guild bigint NOT NULL,
    thread bigint NOT NULL,
    owner bigint NOT NULL,
    header_message bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    display_id integer DEFAULT 0 NOT NULL
);


--
-- Name: request_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.request_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: request_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.request_id_seq OWNED BY public.request.id;


--
-- Name: request_info; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.request_info (
    request integer NOT NULL,
    title character varying(100) NOT NULL,
    detail text NOT NULL,
    state public.state DEFAULT 'opening'::public.state NOT NULL,
    tags character varying(20)[]
);


--
-- Name: subscription; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.subscription (
    "user" bigint NOT NULL,
    request integer NOT NULL
);


--
-- Name: todo; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.todo (
    "user" bigint NOT NULL,
    content character varying(2000)[] NOT NULL
);


--
-- Name: request id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.request ALTER COLUMN id SET DEFAULT nextval('public.request_id_seq'::regclass);


--
-- Name: guild key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.guild
    ADD CONSTRAINT key PRIMARY KEY (id);


--
-- Name: request request_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.request
    ADD CONSTRAINT request_pk PRIMARY KEY (id);


--
-- Name: subscription subscription_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subscription
    ADD CONSTRAINT subscription_pk PRIMARY KEY ("user", request);


--
-- Name: request_info table_name_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.request_info
    ADD CONSTRAINT table_name_pkey PRIMARY KEY (request);


--
-- Name: todo todo_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.todo
    ADD CONSTRAINT todo_pkey PRIMARY KEY ("user");


--
-- PostgreSQL database dump complete
--

