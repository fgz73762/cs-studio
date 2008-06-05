package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TODO table name: AMS_FLAG columns: CFLAGNAME ("ReplicationState") (ID)
 * SFLAGVALUE ( 0 (repliziert/synchron), 1 (?), 2 (soll/darf repliziert
 * werden) oder 3 (starte replizieren) kp was diese bedeuten)
 * 
 * public final static short FLAGVALUE_SYNCH_IDLE = 0; public final static
 * short FLAGVALUE_SYNCH_FMR_RPL = 1; public final static short
 * FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED = 2; Filtermanger hat eine
 * Syncronizations Nachricht an den Distributor gesendet public final static
 * short FLAGVALUE_SYNCH_DIST_RPL = 3; public final static short
 * FLAGVALUE_SYNCH_DIST_NOTIFY_FMR = 4;
 */
@Entity
@Table(name="AMS_FLAG")
public class ReplicationStateDTO {
	// TODO Design this DTO to fit table
	// TODO Register this DTO class in mapping file
	// TODO Register this DTO class in annotation configuration

	public final static String DB_FLAG_NAME = "ReplicationState";
	
	@Id
	@Column(unique=true,name="CFLAGNAME")
	private String flagName = DB_FLAG_NAME;
	
	@Column(name="SFLAGVALUE")
	private short value = ReplicationState.INVALID_STATE.getDbValue();
	
	public ReplicationState getReplicationState() {
		return ReplicationState.valueOf(getValue());
	}
	
	public void setReplicationState(ReplicationState replicationState) {
		setValue(replicationState.getDbValue());
	}
	
	
	public static enum ReplicationState {
		/**
		 * Es muss nicht Repliziert werden
		 */
		FLAGVALUE_SYNCH_IDLE((short) 0),

		/**
		 * FilterManager beginnt zu Replizieren und schreibt history Eintrag.
		 * 
		 * <pre>
		 * private static void logHistoryRplStart(java.sql.Connection conDb, boolean bStart) {
		 * 	try {
		 * 		HistoryTObject history = new HistoryTObject();
		 * 
		 * 		history.setTimeNew(new Date(System.currentTimeMillis()));
		 * 		history.setType(&quot;Config Synch&quot;);
		 * 
		 * 		if (bStart)
		 * 			history
		 * 					.setDescription(&quot;Filtermanager stops normal work, wait for Distributor.&quot;);
		 * 		else
		 * 			history
		 * 					.setDescription(&quot;Filtermanager got config replication end, goes to normal work.&quot;);
		 * 
		 * 		HistoryDAO.insert(conDb, history);
		 * 		Log.log(Log.INFO, history.getDescription()); // history.getHistoryID() + &quot;. &quot;
		 * 	} catch (Exception ex) {
		 * 		Log.log(Log.FATAL, &quot;exception at history logging start=&quot; + bStart, ex);
		 * 	}
		 * }
		 * </pre>
		 */
		FLAGVALUE_SYNCH_FMR_RPL((short) 1),

		/**
		 * FilterManager sendet eine Synchronizationsnachricht ueber das
		 * Nachrichten Topic an den Distributor und wartet auf Antwort.
		 */
		FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED((short) 2),

		/**
		 * Distributor leitet Replication ein.
		 */
		FLAGVALUE_SYNCH_DIST_RPL((short) 3),

		/**
		 * Distributor ist fertig mit Replizieren und sendet Command an das interne JMS Topic.
		 * Der Distributor setzt den Status wieder auf FLAGVALUE_SYNCH_IDLE (0).
		 */
		FLAGVALUE_SYNCH_DIST_NOTIFY_FMR((short) 4),

		/**
		 * Ungültiger Zustand. Daten Fehler.
		 */
		INVALID_STATE((short) -1);

		/**
		 * Wert wie er in der Datenbank steht
		 */
		private final short dbValue;

		ReplicationState(short dbValue) {
			this.dbValue = dbValue;
		}

		short getDbValue() {
			return dbValue;
		}

		static ReplicationState valueOf(short dbValue) {
			switch (dbValue) {
			case 0:
				return FLAGVALUE_SYNCH_IDLE;
			case 1:
				return FLAGVALUE_SYNCH_FMR_RPL;
			case 2:
				return FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED;
			case 3:
				return FLAGVALUE_SYNCH_DIST_RPL;
			case 4:
				return FLAGVALUE_SYNCH_DIST_NOTIFY_FMR;
			default:
				return INVALID_STATE;
			}
		}
	}

	@SuppressWarnings("unused")
	private String getFlagName() {
		return flagName;
	}

	@SuppressWarnings("unused")
	private void setFlagName(String flagName) {
		this.flagName = flagName;
	}

	@SuppressWarnings("unused")
	private short getValue() {
		return value;
	}

	@SuppressWarnings("unused")
	private void setValue(short value) {
		this.value = value;
	}


}
