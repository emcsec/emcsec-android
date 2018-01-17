package com.aspanta.emcsec.db.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;

import com.aspanta.emcsec.db.room.addressBook.BtcAddressBook;
import com.aspanta.emcsec.db.room.addressBook.BtcAddressBookDao;
import com.aspanta.emcsec.db.room.addressBook.EmcAddressBook;
import com.aspanta.emcsec.db.room.addressBook.EmcAddressBookDao;
import com.aspanta.emcsec.db.room.historyPojos.BtcTransaction;
import com.aspanta.emcsec.db.room.historyPojos.BtcTransactionDao;
import com.aspanta.emcsec.db.room.historyPojos.EmcTransaction;
import com.aspanta.emcsec.db.room.historyPojos.EmcTransactionDao;
import com.aspanta.emcsec.db.room.utxosUnconfirmed.UTXOBitcoinAlreadyUsed;
import com.aspanta.emcsec.db.room.utxosUnconfirmed.UTXOBitcoinAlreadyUsedDao;
import com.aspanta.emcsec.db.room.utxosUnconfirmed.UTXOBitcoinFromChange;
import com.aspanta.emcsec.db.room.utxosUnconfirmed.UTXOBitcoinFromChangeDao;
import com.aspanta.emcsec.db.room.utxosUnconfirmed.UTXOEmercoinAlreadyUsed;
import com.aspanta.emcsec.db.room.utxosUnconfirmed.UTXOEmercoinAlreadyUsedDao;
import com.aspanta.emcsec.db.room.utxosUnconfirmed.UTXOEmercoinFromChange;
import com.aspanta.emcsec.db.room.utxosUnconfirmed.UTXOEmercoinFromChangeDao;

@Database(entities = {BtcAddressForChange.class, EmcAddressForChange.class, BtcAddress.class,
        EmcAddress.class, BtcAddressBook.class, EmcAddressBook.class, EmcTransaction.class,
        BtcTransaction.class, UTXOEmercoinFromChange.class, UTXOEmercoinAlreadyUsed.class,
        UTXOBitcoinFromChange.class, UTXOBitcoinAlreadyUsed.class},
        version = 4, exportSchema = false)
public abstract class MainDatabase extends RoomDatabase {

    public abstract BtcAddressDao btcAddressDao();

    public abstract EmcAddressDao emcAddressDao();

    public abstract BtcAddressBookDao btcAddressBookDao();

    public abstract EmcAddressBookDao emcAddressBookDao();

    public abstract BtcAddressForChangeDao btcAddressForChangeDao();

    public abstract EmcAddressForChangeDao emcAddressForChangeDao();

    public abstract BtcTransactionDao btcTransactionDao();

    public abstract EmcTransactionDao emcTransactionDao();

    public abstract UTXOEmercoinFromChangeDao utxoEmercoinFromChangeDao();

    public abstract UTXOEmercoinAlreadyUsedDao utxoEmercoinAlreadyUsedDao();

    public abstract UTXOBitcoinFromChangeDao utxoBitcoinFromChangeDao();

    public abstract UTXOBitcoinAlreadyUsedDao utxoBitcoinAlreadyUsedDao();

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE `emc_transaction` (`uid` INTEGER PRIMARY KEY, `date` TEXT, " +
                    "`address` TEXT, `category` TEXT, `amount` TEXT, `fee` TEXT, `tx_id` TEXT, `block` TEXT)");

            database.execSQL("CREATE TABLE `btc_transaction` (`uid` INTEGER PRIMARY KEY, `date` TEXT, " +
                    "`address` TEXT, `category` TEXT, `amount` TEXT, `fee` TEXT, `tx_id` TEXT, `block` TEXT)");
        }
    };
}
