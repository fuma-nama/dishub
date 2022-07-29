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
    user_role bigint NOT NULL,
    admin_role bigint,
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
    header_message bigint NOT NULL
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
    guild bigint NOT NULL,
    request integer NOT NULL,
    title character varying(100) NOT NULL,
    detail text NOT NULL,
    state public.state DEFAULT 'opening'::public.state NOT NULL
);


ALTER TABLE public.request_info OWNER TO postgres;

--
-- Name: subscription; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.subscription (
    "user" bigint NOT NULL,
    guild bigint NOT NULL,
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
-- Data for Name: guild; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.guild (id, container, user_role, admin_role, manager_role) FROM stdin;
684766026776576052      1001473645329268786     684766026776576052      684970314136027150      684970314136027150
\.


--
-- Data for Name: request; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.request (id, guild, thread, owner, header_message) FROM stdin;
21      684766026776576052      1002213666227241000     572329183334891520      1002213667682656266
23      684766026776576052      1002495711532830760     572329183334891520      1002495713785155645
25      684766026776576052      1002528942663675934     898937034402578523      1002528944249114644
26      684766026776576052      1002531307445829632     898937034402578523      1002531308888666202
27      684766026776576052      1002576198683078677     898937034402578523      1002576200117530734
\.


--
-- Data for Name: request_info; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.request_info (guild, request, title, detail, state) FROM stdin;
684766026776576052      21      Hello World     I am MONEY      opening
684766026776576052      23      Kill Kane       Just kill him   closed
684766026776576052      25      Hello World     I need some MONEY       opening
684766026776576052      26      fds     fds     closed
684766026776576052      27      Hello World     Hey Boy, I am MONEY!!!! closed
\.


--
-- Data for Name: subscription; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.subscription ("user", guild, request) FROM stdin;
572329183334891520      684766026776576052      21
572329183334891520      684766026776576052      23
898937034402578523      684766026776576052      25
898937034402578523      684766026776576052      26
898937034402578523      684766026776576052      27
\.


--
-- Data for Name: todo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.todo ("user", content) FROM stdin;
572329183334891520      {fdsa}
\.


--
-- Name: request_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.request_id_seq', 27, true);


--
-- Name: guild key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.guild
    ADD CONSTRAINT key PRIMARY KEY (id);


--
-- Name: request request_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.request
    ADD CONSTRAINT request_pk PRIMARY KEY (id, guild);


--
-- Name: subscription subscription_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subscription
    ADD CONSTRAINT subscription_pk PRIMARY KEY ("user", guild, request);


--
-- Name: request_info table_name_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.request_info
    ADD CONSTRAINT table_name_pkey PRIMARY KEY (guild, request);


--
-- Name: todo todo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.todo
    ADD CONSTRAINT todo_pkey PRIMARY KEY ("user");


--
-- Name: request_info request_header_request_guild_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.request_info
    ADD CONSTRAINT request_header_request_guild_id_fk FOREIGN KEY (guild, request) REFERENCES public.request(guild, id) ON DELETE CASCADE;


--
-- Name: subscription subscription_guild_request_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.subscription
    ADD CONSTRAINT subscription_guild_request_fkey FOREIGN KEY (guild, request) REFERENCES public.request(guild, id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--