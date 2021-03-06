package fr.eservices.drive.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import fr.eservices.drive.model.StatusHistoryEntity;
import fr.eservices.drive.model.TimeObject;
import fr.eservices.drive.web.HistorySource;

//@Component
//@Qualifier("mock")
public class HistorySourceMock implements HistorySource {
	
	private List<StatusHistory> status;
	
	public HistorySourceMock() {
		status = new ArrayList<StatusHistory>( Arrays.asList(
			createStatus( "ORDERED",          "2017-11-28T10:00:00Z" ),
			createStatus( "READY_TO_DELIVER", "2017-11-28T10:00:00Z" )
		));
	}

	SimpleDateFormat sdf = new SimpleDateFormat("");
	private StatusHistory createStatus(String statusName, String dateTime) {
		StatusHistory status = new StatusHistory();
		status.setStatus( Status.valueOf(statusName) );
		status.setStatusDate( DatatypeConverter.parseDate(dateTime).getTime() );
		TimeObject t = new TimeObject();
		Date now = new Date();
		t.day = DateFormat.getDateInstance(DateFormat.SHORT).format(now);
		t.time = DateFormat.getTimeInstance(DateFormat.SHORT).format(now);
		t.locale = Locale.getDefault().toString();
		t.timestamp = (long) (now.getTime() / 1000);
		status.setTimeObject(t);
		return status;
	}

	@Override
	public List<StatusHistory> orderHistory(int orderId) {
		return status;
	}
	
	@Override
	public void addHistoryStatus(int orderId, StatusHistory statusHistory) 
	throws DataException {
		if ( orderId == 666 ) throw new DataException("No such order");
		status.add(statusHistory);
	}

	@Override
	public void addHistoryAllStatusl(int orderId, List<StatusHistory> histories) throws DataException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<StatusHistoryEntity> orderAllHistory(int orderId) throws DataException {
		// TODO Auto-generated method stub
		return null;
	}
}
