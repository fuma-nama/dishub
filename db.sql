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
-- Name: state; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.state AS ENUM (
    'opening',
    'closed',
    'processing'
);


ALTER TYPE public.state OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: guild; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.guild (
    id bigint NOT NULL,
    container bigint NOT NULL,
    manager_role bigint
);


ALTER TABLE public.guild OWNER TO postgres;

--
-- Name: request; Type: TABLE; Schema: public; Owner: postgres
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


ALTER TABLE public.request OWNER TO postgres;

--
-- Name: request_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.request_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.request_id_seq OWNER TO postgres;

--
-- Name: request_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.request_id_seq OWNED BY public.request.id;


--
-- Name: request_info; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.request_info (
    request integer NOT NULL,
    title character varying(100) NOT NULL,
    detail text NOT NULL,
    state public.state DEFAULT 'opening'::public.state NOT NULL,
    tags character varying(20)[]
);


ALTER TABLE public.request_info OWNER TO postgres;

--
-- Name: subscription; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.subscription (
    "user" bigint NOT NULL,
    request integer NOT NULL
);


ALTER TABLE public.subscription OWNER TO postgres;

--
-- Name: todo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.todo (
    "user" bigint NOT NULL,
    content character varying(2000)[] NOT NULL
);


ALTER TABLE public.todo OWNER TO postgres;

--
-- Name: request id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.request ALTER COLUMN id SET DEFAULT nextval('public.request_id_seq'::regclass);


--
-- Name: guild key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.guild
    ADD CONSTRAINT key PRIMARY KEY (id);


--
-- Name: request request_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.request
    ADD CONSTRAINT request_pk PRIMARY KEY (id);


--
-- Name: subscription subscription_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subscription
    ADD CONSTRAINT subscription_pk PRIMARY KEY ("user", request);


--
-- Name: request_info table_name_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.request_info
    ADD CONSTRAINT table_name_pkey PRIMARY KEY (request);


--
-- Name: todo todo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.todo
    ADD CONSTRAINT todo_pkey PRIMARY KEY ("user");


--
-- PostgreSQL database dump complete
--
