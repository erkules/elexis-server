package info.elexis.server.core.connector.elexis.jpa.model.annotated;

import info.elexis.server.core.connector.elexis.jpa.model.annotated.AbstractDBObjectIdDeleted;
import info.elexis.server.core.connector.elexis.jpa.model.annotated.Kontakt;
import java.time.LocalDate;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.6.2.v20151217-rNA", date="2016-02-11T11:54:06")
@StaticMetamodel(Prescription.class)
public class Prescription_ { 

    public static volatile SingularAttribute<Prescription, String> artikelID;
    public static volatile SingularAttribute<Prescription, String> rezeptID;
    public static volatile SingularAttribute<Prescription, AbstractDBObjectIdDeleted> artikel;
    public static volatile SingularAttribute<Prescription, String> anzahl;
    public static volatile SingularAttribute<Prescription, Kontakt> patientID;
    public static volatile SingularAttribute<Prescription, String> dosis;
    public static volatile SingularAttribute<Prescription, String> bemerkung;
    public static volatile SingularAttribute<Prescription, LocalDate> dateUntil;
    public static volatile SingularAttribute<Prescription, LocalDate> dateFrom;

}