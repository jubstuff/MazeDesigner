-- Labirinto
CREATE TABLE Labirinto
(
	codice  	  NUMBER(8)    NOT NULL,
	nome          VARCHAR2(50) NOT NULL,
	dimX          NUMBER(8)    NOT NULL,
	dimY          NUMBER(8)    NOT NULL,
	uscita        NUMBER(8),
	entrata       NUMBER(8),
	is_bozza      CHAR(1)      DEFAULT 'T'
)
;

ALTER TABLE Labirinto ADD CONSTRAINT PK_Labirinto
	PRIMARY KEY (codice);

ALTER TABLE Labirinto ADD CONSTRAINT UQ_Labirinto_nome 
	UNIQUE (nome);

-- Casella
CREATE TABLE Casella
(
	codice   NUMBER(8) NOT NULL,
	posX         NUMBER(8) NOT NULL,
	posY         NUMBER(8) NOT NULL,
	labirinto    NUMBER(8),
	tipo         CHAR(1) DEFAULT 'M' NOT NULL,
	descrizione  VARCHAR2(100)
);

ALTER TABLE Casella ADD CONSTRAINT PK_Casella
	PRIMARY KEY (codice);

-- Collegamento
CREATE TABLE Collegamento
(
	codice       NUMBER(8) NOT NULL,
	origine       NUMBER(8) NOT NULL,
	destinazione  NUMBER(8) NOT NULL
);
ALTER TABLE Collegamento ADD CONSTRAINT PK_Collegamento
	PRIMARY KEY (codice);


--Speciale
CREATE TABLE Speciale
(
	id_speciale            NUMBER(8) NOT NULL,
	modificatorePunteggio  NUMBER(8) DEFAULT 0 NOT NULL,
	tipo 				CHAR(1)  DEFAULT 'S' NOT NULL
);

ALTER TABLE Speciale ADD CONSTRAINT PK_Speciale
	PRIMARY KEY (id_speciale);



	

	




Create table temp
(
 id_casella   NUMBER(8)
);	

	-- Chiavi esterne
ALTER TABLE Labirinto ADD CONSTRAINT FK_Labirinto_Casella
	FOREIGN KEY (entrata) REFERENCES Casella (codice)
	ON DELETE SET NULL;

ALTER TABLE Labirinto ADD CONSTRAINT FK_Labirinto_Casella2
	FOREIGN KEY (uscita) REFERENCES Casella (codice)
	ON DELETE SET NULL;

ALTER TABLE Casella ADD CONSTRAINT FK_Casella_Labirinto
	FOREIGN KEY (labirinto) REFERENCES Labirinto (codice)
	ON DELETE CASCADE;

ALTER TABLE Collegamento ADD CONSTRAINT FK_Collegamento_Casella
	FOREIGN KEY (origine) REFERENCES Casella (codice)
	ON DELETE CASCADE;
 
ALTER TABLE Collegamento ADD CONSTRAINT FK_Collegamento_Casella2
	FOREIGN KEY (destinazione) REFERENCES Casella (codice)
	ON DELETE CASCADE;

  -- Trigger & Vincoli

ALTER TABLE Casella ADD CONSTRAINT posizione_unica_labirinto
	UNIQUE(posX, posY, labirinto);

ALTER TABLE Labirinto ADD CONSTRAINT entrata_uscita
	CHECK(entrata <> uscita);

ALTER TABLE Labirinto ADD CONSTRAINT dimensioni_positive 
	CHECK(dimX > 0 AND dimY > 0);
	
ALTER TABLE Casella ADD CONSTRAINT tipo CHECK (
	tipo IN ('V', 'S', 'M')
);

ALTER TABLE Casella ADD CONSTRAINT UQ_Casella 
	UNIQUE(codice, tipo);
/

CREATE OR REPLACE TRIGGER esiste_percorso
AFTER UPDATE OF is_bozza ON Labirinto
For each row
WHEN (NEW.is_bozza = 'F')
	--dichiarazioni
  Declare
	v_count NUMBER;
BEGIN
	DELETE FROM Temp;
	--inserisco nella tabella Temp il nodo iniziale
	INSERT INTO Temp VALUES(:NEW.entrata);
	LOOP
		INSERT INTO Temp (
			SELECT C.destinazione
			FROM Collegamento C JOIN Temp T
			ON C.origine = T.id_casella
			WHERE C.destinazione NOT IN (
				SELECT id_casella
				FROM Temp
			)
		);
		
		EXIT WHEN SQL%NOTFOUND; --è ok perché è una insert
	END LOOP;
	
	SELECT COUNT(*)
	INTO v_count
	FROM TEMP
	WHERE id_casella = :NEW.uscita;
	
	IF v_count < 1 THEN
		raise_application_error(-20001, 'Deve esistere un percorso tra entrata e uscita');
	END IF;
END;
/

-- Trigger auto_increment
create sequence codice_labirinto_sequence
increment by 1 start with 1
nomaxvalue;  
/
create or replace trigger codice_labirinto_autoincrement 
before insert on labirinto
for each row 
begin
	Select codice_labirinto_sequence.nextval into :new.codice from dual;
end;
/
create sequence codice_casella_sequence
increment by 1 start with 1
nomaxvalue;  
/
create or replace trigger codice_casella_autoincrement 
before insert on casella
for each row 
begin
	Select codice_casella_sequence.nextval into :new.codice from dual;
end;
/
create sequence codice_collegamento_sequence
increment by 1 start with 1
nomaxvalue;  
/
create or replace trigger codice_coll_autoincrement 
before insert on collegamento
for each row 
begin
	Select codice_collegamento_sequence.nextval into :new.codice from dual;
end;
/
-- Trigger inizializzazione labirinto

Create or replace trigger inizializza_labirinto
After insert on labirinto
for each row
begin
	for i in 0 .. :new.dimy - 1
	loop
            for j in 0 .. :new.dimx - 1
            loop
                insert into Casella (labirinto,posy,posx) Values (:new.codice,i,j);
            end loop;
	end loop;
end;

/

CREATE OR REPLACE TRIGGER bi_speciale
BEFORE INSERT ON Speciale
FOR EACH ROW
BEGIN
    UPDATE Casella SET tipo = 'S' WHERE codice = :NEW.id_speciale;
END;
/
CREATE OR REPLACE TRIGGER ad_speciale
AFTER DELETE ON Speciale
FOR EACH ROW
BEGIN
	UPDATE Casella SET tipo = 'V' WHERE codice = :OLD.id_speciale;
END;
/

Alter table collegamento add constraint UQ_orig_dest
unique(origine,destinazione)
/


CREATE OR REPLACE PROCEDURE init_caselle_labirinto (cod_lab in number, dimx_lab in number, dimy_lab in number)
IS
BEGIN
    FOR i IN 0 .. dimy_lab - 1
    LOOP
        FOR j IN 0 .. dimx_lab - 1 
        LOOP
        INSERT INTO casella (labirinto,posy,posx) VALUES ( cod_lab, i, j);
        END LOOP;
    END LOOP;
END;
/
CREATE OR REPLACE PROCEDURE reset_labirinto (cod_lab in number, dimx_lab in number, dimy_lab in number)
IS
BEGIN
    UPDATE labirinto SET entrata=null, uscita=null, is_bozza='T'
    WHERE codice = cod_lab;
    
    DELETE FROM casella WHERE labirinto = cod_lab;
    
    init_caselle_labirinto(cod_lab, dimx_lab, dimy_lab);
END;
/