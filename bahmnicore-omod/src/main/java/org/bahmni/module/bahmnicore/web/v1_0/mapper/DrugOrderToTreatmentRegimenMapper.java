package org.bahmni.module.bahmnicore.web.v1_0.mapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.bahmni.module.bahmnicoreui.mapper.DoseInstructionMapper;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.module.bahmniemrapi.drugogram.contract.RegimenRow;
import org.openmrs.module.bahmniemrapi.drugogram.contract.TreatmentRegimen;
import org.openmrs.module.emrapi.encounter.ConceptMapper;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Component
public class DrugOrderToTreatmentRegimenMapper {

	public TreatmentRegimen map(List<Order> drugOrders, Set<Concept> headersConfig) throws ParseException {
		TreatmentRegimen treatmentRegimen = new TreatmentRegimen();
		Set<Concept> headers = new LinkedHashSet<>();
		SortedSet<RegimenRow> regimenRows = new TreeSet<>(new RegimenRow.RegimenComparator());

//		filterDrugsWhichDoesntHaveDose(drugOrders);
		constructRegimenRowsForDrugsWhichAreStartedAndStoppedOnSameDate(regimenRows, drugOrders, headers);
		filterDrugsWhichAreStoppedBeforeScheduledDate(drugOrders);

		for (Order order : drugOrders) {
			DrugOrder drugOrder = (DrugOrder) order;
			headers.add(drugOrder.getConcept());

			constructRegimenRows(drugOrders, regimenRows, drugOrder);
		}
		Set<EncounterTransaction.Concept> headersConcept;
		if (!CollectionUtils.isEmpty(headersConfig))
			headersConcept = mapHeaders(headersConfig);
		else
			headersConcept = mapHeaders(headers);
		treatmentRegimen.setHeaders(headersConcept);
		treatmentRegimen.setRows(regimenRows);
		return treatmentRegimen;
	}

	private void filterDrugsWhichAreStoppedBeforeScheduledDate(List<Order> drugOrders) {
		CollectionUtils.filter(drugOrders, new Predicate() {

			@Override
			public boolean evaluate(Object o) {
				DrugOrder drugOrder = (DrugOrder) o;
				try {
					Date autoExpiryDate = drugOrder.getDateStopped() != null ?
							getOnlyDate(drugOrder.getDateStopped()) :
							getOnlyDate(drugOrder.getAutoExpireDate());
					Date dateActivated = drugOrder.getScheduledDate() != null ?
							getOnlyDate(drugOrder.getScheduledDate()) :
							getOnlyDate(drugOrder.getDateActivated());
					if (autoExpiryDate == null)
						return true;
					return dateActivated.before(autoExpiryDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return false;
			}
		});
	}

	private void constructRegimenRowsForDrugsWhichAreStartedAndStoppedOnSameDate(SortedSet<RegimenRow> regimenRows,
	                                                                             List<Order> drugOrders,
	                                                                             Set<Concept> headers)
			throws ParseException {
		Collection drugOrdersStartedAndStoppedOnSameDate = CollectionUtils.select(drugOrders, new Predicate() {

			@Override
			public boolean evaluate(Object o) {
				DrugOrder drugOrder = (DrugOrder) o;
				try {
					Date startDate = drugOrder.getScheduledDate() != null ?
							getOnlyDate(drugOrder.getScheduledDate()) :
							getOnlyDate(drugOrder.getDateActivated());
					Date stopDate = drugOrder.getDateStopped() != null ?
							getOnlyDate(drugOrder.getDateStopped()) :
							getOnlyDate(drugOrder.getAutoExpireDate());
					if (stopDate == null)
						return false;
					return startDate.equals(stopDate);
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
				return false;
			}
		});

		for (int i = 0; i < drugOrdersStartedAndStoppedOnSameDate.size(); i++) {
			DrugOrder drugOrder = (DrugOrder) CollectionUtils.get(drugOrdersStartedAndStoppedOnSameDate, i);
			headers.add(drugOrder.getConcept());
			SortedSet<RegimenRow> dateActivatedRow = findOrCreateRowForDateActivated(regimenRows, drugOrder);
			SortedSet<RegimenRow> dateStoppedRow = findOrCreateRowForForDateStopped(regimenRows, drugOrder);

			Date stoppedDate =
					drugOrder.getDateStopped() != null ? drugOrder.getDateStopped() : drugOrder.getAutoExpireDate();
			if (i > 0 && dateActivatedRow.iterator().next().getDate().equals(getOnlyDate(stoppedDate))
					&& dateActivatedRow.size() > 1) {
				constructRowForDateActivated(drugOrder, dateActivatedRow.iterator().next());
				constructRowForDateStopped(drugOrder, stoppedDate, (RegimenRow) CollectionUtils.get(dateStoppedRow, 1));
			} else {
				constructRowsForDateActivated(dateActivatedRow, drugOrder);
				constructRowsForDateStopped(dateStoppedRow, drugOrder);
			}

			regimenRows.addAll(dateActivatedRow);
			regimenRows.addAll(dateStoppedRow);
		}

		drugOrders.removeAll(drugOrdersStartedAndStoppedOnSameDate);
		for (Order order : drugOrders) {
			DrugOrder drugOrder = (DrugOrder) order;

			String drugName = drugOrder.getConcept().getName().getName();
			String dosage = getDose(drugOrder);

			for (RegimenRow regimenRow : regimenRows) {
				Date dateActivated = drugOrder.getScheduledDate() != null ?
						getOnlyDate(drugOrder.getScheduledDate()) :
						getOnlyDate(drugOrder.getDateActivated());
				Date dateStopped = drugOrder.getAutoExpireDate() != null ?
						getOnlyDate(drugOrder.getAutoExpireDate()) :
						getOnlyDate(drugOrder.getDateStopped());
				if (orderCrossDate(drugOrder, regimenRow.getDate())) {
					if (dateStopped == null)
						regimenRow.addDrugs(drugName, dosage);
					else if ( !"Stop".equals(regimenRow.getDrugs().get(drugName))) {
						regimenRow.addDrugs(drugName, dosage);
					}
					else if (drugOrder.getAction().equals(Order.Action.REVISE)
							&& regimenRow.getDate().equals(dateActivated)) {
						regimenRow.addDrugs(drugName, dosage);
					}
				}
			}
		}
	}

	private Set<EncounterTransaction.Concept> mapHeaders(Set<Concept> headers) {
		Set<EncounterTransaction.Concept> headersConcept = new LinkedHashSet<>();
        for (Concept header : headers) {
				headersConcept.add(new ConceptMapper().map(header));
			}
        return headersConcept;
	}

	private void constructRegimenRows(List<Order> drugOrders, SortedSet<RegimenRow> regimenRows, DrugOrder drugOrder)
			throws ParseException {
		SortedSet<RegimenRow> dateActivatedRow = findOrCreateRowForDateActivated(regimenRows, drugOrder);
		SortedSet<RegimenRow> dateStoppedRow = findOrCreateRowForForDateStopped(regimenRows, drugOrder);

		for (Order order1 : drugOrders) {
			DrugOrder drugOrder1 = (DrugOrder) order1;

			constructRowsForDateActivated(dateActivatedRow, drugOrder1);
			if (dateStoppedRow != null)
				constructRowsForDateStopped(dateStoppedRow, drugOrder1);

		}
		regimenRows.addAll(dateActivatedRow);
		if (dateStoppedRow != null)
			regimenRows.addAll(dateStoppedRow);
	}

	private void constructRowsForDateStopped(SortedSet<RegimenRow> dateStoppedRow, DrugOrder drugOrder1)
			throws ParseException {
		Date stoppedDate =
				drugOrder1.getDateStopped() != null ? drugOrder1.getDateStopped() : drugOrder1.getAutoExpireDate();

        for (RegimenRow regimenRow : dateStoppedRow) {
            constructRowForDateStopped(drugOrder1, stoppedDate, regimenRow);
        }
	}

	private void constructRowForDateStopped(DrugOrder drugOrder1, Date stoppedDate, RegimenRow regimenRow)
			throws ParseException {

		if (orderCrossDate(drugOrder1, regimenRow.getDate())) {
			String drugName = drugOrder1.getConcept().getName().getName();
			String dosage = getDose(drugOrder1);
			Date startDate = drugOrder1.getScheduledDate() != null ? drugOrder1.getScheduledDate(): drugOrder1.getDateActivated();
			if(MapUtils.isNotEmpty(regimenRow.getDrugs()) && regimenRow.getDrugs().keySet().contains(drugName) && !"Stop".equals(regimenRow.getDrugs().get(drugName)) && !drugOrder1.getAction().equals(Order.Action.REVISE)) return;
			if (stoppedDate == null && (startDate.before(regimenRow.getDate()) || startDate.equals(regimenRow.getDate()) ))
				regimenRow.addDrugs(drugName, dosage);
			else if (stoppedDate != null && getOnlyDate(stoppedDate).equals(regimenRow.getDate()))
				regimenRow.addDrugs(drugName, "Stop");
			else if (stoppedDate != null )
				regimenRow.addDrugs(drugName, dosage);
		}
	}

	private void constructRowsForDateActivated(SortedSet<RegimenRow> dateActivatedRow, DrugOrder drugOrder1)
			throws ParseException {
		for (RegimenRow regimenRow : dateActivatedRow) {
			constructRowForDateActivated(drugOrder1, regimenRow);
		}
	}

	private void constructRowForDateActivated(DrugOrder drugOrder1, RegimenRow regimenRow) throws ParseException {
		Date dateActivated = drugOrder1.getScheduledDate() != null ?
				getOnlyDate(drugOrder1.getScheduledDate()) :
				getOnlyDate(drugOrder1.getDateActivated());
		Date dateStopped = drugOrder1.getAutoExpireDate() != null ?
				getOnlyDate(drugOrder1.getAutoExpireDate()) :
				getOnlyDate(drugOrder1.getDateStopped());
		String drugName = drugOrder1.getConcept().getName().getName();

		String dosage = getDose(drugOrder1);
		if(MapUtils.isNotEmpty(regimenRow.getDrugs()) && regimenRow.getDrugs().keySet().contains(drugName) && !"Stop".equals(regimenRow.getDrugs().get(drugName)) && !drugOrder1.getAction().equals(Order.Action.REVISE)) return;
		if (dateStopped == null && (dateActivated.before(regimenRow.getDate()) || dateActivated.equals(regimenRow.getDate())) ) {
			regimenRow.addDrugs(drugName, dosage);
		}
		else if (dateStopped != null && orderCrossDate(drugOrder1, regimenRow.getDate()) && !"Stop"
				.equals(regimenRow.getDrugs().get(drugName))) {
			regimenRow.addDrugs(drugName, dosage);
		}
		else if (dateStopped != null && orderCrossDate(drugOrder1, regimenRow.getDate()) && drugOrder1.getAction().equals(Order.Action.REVISE)
				&& regimenRow.getDate().equals(dateActivated)) {
			regimenRow.addDrugs(drugName, dosage);
		}
	}

	private boolean orderCrossDate(DrugOrder drugOrder, Date date) throws ParseException {
		Date autoExpiryDate = drugOrder.getDateStopped() != null ?
				getOnlyDate(drugOrder.getDateStopped()) :
				getOnlyDate(drugOrder.getAutoExpireDate());
		Date dateActivated = drugOrder.getScheduledDate() != null ?
				getOnlyDate(drugOrder.getScheduledDate()) :
				getOnlyDate(drugOrder.getDateActivated());
		if (autoExpiryDate == null)
			return true;
		return dateActivated.equals(date) || autoExpiryDate.equals(date) || dateActivated.before(date) && autoExpiryDate
				.after(date);
	}

	private SortedSet<RegimenRow> findOrCreateRowForDateActivated(SortedSet<RegimenRow> regimenRows, DrugOrder drugOrder)
			throws ParseException {
		Date date = drugOrder.getScheduledDate() != null ?
				getOnlyDate(drugOrder.getScheduledDate()) :
				getOnlyDate(drugOrder.getDateActivated());

		return getRegimenRowFor(regimenRows, date);
	}

	private SortedSet<RegimenRow> findOrCreateRowForForDateStopped(SortedSet<RegimenRow> regimenRows, DrugOrder drugOrder)
			throws ParseException {
		Date date = drugOrder.getDateStopped() != null ?
				getOnlyDate(drugOrder.getDateStopped()) :
				getOnlyDate(drugOrder.getAutoExpireDate());
		if (date == null)
			return null;
		return getRegimenRowFor(regimenRows, date);
	}

	private SortedSet<RegimenRow> getRegimenRowFor(SortedSet<RegimenRow> regimenRows, Date date) {
		SortedSet<RegimenRow> foundRows = new TreeSet<>(new RegimenRow.RegimenComparator());
		for (RegimenRow regimenRow : regimenRows) {
			if (regimenRow.getDate().equals(date)) {
				foundRows.add(regimenRow);
			}
		}
		if (CollectionUtils.isNotEmpty(foundRows)) {
			return foundRows;
		}

		RegimenRow regimenRow = new RegimenRow();
		regimenRow.setDate(date);
		foundRows.add(regimenRow);
		return foundRows;
	}

	private Date getOnlyDate(Date date) throws ParseException {
		if(date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.parse(sdf.format(date));
	}

	private String getDose(DrugOrder drugOrder) {
		String dosage = null;
		if (drugOrder.getFrequency() == null) {
			try {
				dosage = DoseInstructionMapper.getFrequency(drugOrder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if(drugOrder.getDose() != null){
				dosage = drugOrder.getDose().toString();
			}
			else{
				dosage = "";
			}
		}
		return dosage;
	}
}
