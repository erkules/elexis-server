package info.elexis.server.core.connector.elexis.services;

import java.time.LocalDate;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import info.elexis.server.core.connector.elexis.billable.VerrechenbarArtikelstammItem;
import info.elexis.server.core.connector.elexis.billable.VerrechenbarLabor2009Tarif;
import info.elexis.server.core.connector.elexis.billable.VerrechenbarPhysioLeistung;
import info.elexis.server.core.connector.elexis.billable.VerrechenbarTarmedLeistung;
import info.elexis.server.core.connector.elexis.internal.BundleConstants;
import info.elexis.server.core.connector.elexis.jpa.model.annotated.AbstractDBObjectIdDeleted;
import info.elexis.server.core.connector.elexis.jpa.model.annotated.ArtikelstammItem;
import info.elexis.server.core.connector.elexis.jpa.model.annotated.Behandlung;
import info.elexis.server.core.connector.elexis.jpa.model.annotated.Fall;
import info.elexis.server.core.connector.elexis.jpa.model.annotated.Kontakt;
import info.elexis.server.core.connector.elexis.jpa.model.annotated.Labor2009Tarif;
import info.elexis.server.core.connector.elexis.jpa.model.annotated.PhysioLeistung;
import info.elexis.server.core.connector.elexis.jpa.model.annotated.TarmedLeistung;

public class BehandlungService extends AbstractService<Behandlung> {

	public static BehandlungService INSTANCE = InstanceHolder.INSTANCE;

	private static final class InstanceHolder {
		static final BehandlungService INSTANCE = new BehandlungService();
	}

	private BehandlungService() {
		super(Behandlung.class);
	}

	/**
	 * Create a {@link Behandlung} with mandatory attributes
	 * 
	 * @param fall
	 * @param mandator
	 * @return
	 */
	public Behandlung create(Fall fall, Kontakt mandator) {
		em.getTransaction().begin();
		Behandlung cons = create(false);
		em.merge(fall);
		em.merge(mandator);
		cons.setDatum(LocalDate.now());
		cons.setFall(fall);
		cons.setMandant(mandator);
		// TODO fall.getPatient().setInfoElement("LetzteBehandlung", getId());
		em.getTransaction().commit();
		return cons;
	}

	public static IStatus chargeBillableOnBehandlung(Behandlung kons, AbstractDBObjectIdDeleted billableObject,
			Kontakt userContact, Kontakt mandatorContact) {
		if (billableObject instanceof TarmedLeistung) {
			return new VerrechenbarTarmedLeistung((TarmedLeistung) billableObject).add(kons, userContact,
					mandatorContact);
		} else if (billableObject instanceof Labor2009Tarif) {
			return new VerrechenbarLabor2009Tarif((Labor2009Tarif) billableObject).add(kons, userContact,
					mandatorContact);
		} else if (billableObject instanceof PhysioLeistung) {
			return new VerrechenbarPhysioLeistung((PhysioLeistung) billableObject).add(kons, userContact,
					mandatorContact);
		} else if (billableObject instanceof ArtikelstammItem) {
			return new VerrechenbarArtikelstammItem((ArtikelstammItem) billableObject).add(kons, userContact,
					mandatorContact);
		}

		return new Status(Status.ERROR, BundleConstants.BUNDLE_ID,
				"No Verrechenbar wrapper found for " + billableObject.getLabel());
	}

}
