package org.scale7.cassandra.pelops;

import org.apache.cassandra.thrift.Clock;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.scale7.cassandra.pelops.IThriftPool.IConnection;

import static org.scale7.cassandra.pelops.Bytes.fromUTF8;
import static org.scale7.cassandra.pelops.Bytes.nullSafeGet;

/**
 * Facilitates the removal of data at a key-level.
 *
 * @author dominicwilliams
 *
 */
public class RowDeletor extends Operand {

	private final Clock clock;

	/**
	 * Delete a row with a specified key from a specified column family. The function succeeds even if
	 * the row does not exist.
	 * @param columnFamily				The column family from which to delete the row
	 * @param rowKey					The key of the row
	 * @param cLevel					The Cassandra consistency level to be used
	 * @throws Exception
	 */
	public void deleteRow(final String columnFamily, final String rowKey, final ConsistencyLevel cLevel) throws Exception {
		deleteRow(columnFamily, fromUTF8(rowKey), cLevel);
	}

	/**
	 * Delete a row with a specified key from a specified column family. The function succeeds even if
	 * the row does not exist.
	 * @param columnFamily				The column family from which to delete the row
	 * @param rowKey					The key of the row
	 * @param cLevel					The Cassandra consistency level to be used
	 * @throws Exception
	 */
	public void deleteRow(final String columnFamily, final Bytes rowKey, final ConsistencyLevel cLevel) throws Exception {
		IOperation<Void> operation = new IOperation<Void>() {
			@Override
			public Void execute(IConnection conn) throws Exception {

				ColumnPath path = new ColumnPath(columnFamily);
				conn.getAPI().remove(nullSafeGet(rowKey), path, clock, cLevel);
				return null;
			}
		};
		tryOperation(operation);
	}

	protected RowDeletor(IThriftPool thrift) {
		this(thrift, new Clock(System.currentTimeMillis() * 1000));
	}

	protected RowDeletor(IThriftPool thrift, Clock clock) {
		super(thrift);
        this.clock = new Clock(clock);
	}
}
