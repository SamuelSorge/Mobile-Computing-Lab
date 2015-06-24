/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: ILocationService.aidl
 */
package com.example.mobilecomputing.locationmanagement.app;

/**
 * Created by SebastianHesse on 23.06.2015.
 */
public interface ILocationService extends android.os.IInterface {

    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements com.example.mobilecomputing.locationmanagement.app.ILocationService {

        private static final java.lang.String DESCRIPTOR = "com.example.mobilecomputing.locationmanagement.app.ILocationService";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.example.mobilecomputing.locationmanagement.app.ILocationService interface,
         * generating a proxy if needed.
         */
        public static com.example.mobilecomputing.locationmanagement.app.ILocationService asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.example.mobilecomputing.locationmanagement.app.ILocationService))) {
                return ((com.example.mobilecomputing.locationmanagement.app.ILocationService) iin);
            }
            return new com.example.mobilecomputing.locationmanagement.app.ILocationService.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_getLatitude: {
                    data.enforceInterface(DESCRIPTOR);
                    double _result = this.getLatitude();
                    reply.writeNoException();
                    reply.writeDouble(_result);
                    return true;
                }
                case TRANSACTION_getLongitude: {
                    data.enforceInterface(DESCRIPTOR);
                    double _result = this.getLongitude();
                    reply.writeNoException();
                    reply.writeDouble(_result);
                    return true;
                }
                case TRANSACTION_getDistance: {
                    data.enforceInterface(DESCRIPTOR);
                    double _result = this.getDistance();
                    reply.writeNoException();
                    reply.writeDouble(_result);
                    return true;
                }
                case TRANSACTION_getAverageSpeed: {
                    data.enforceInterface(DESCRIPTOR);
                    double _result = this.getAverageSpeed();
                    reply.writeNoException();
                    reply.writeDouble(_result);
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements com.example.mobilecomputing.locationmanagement.app.ILocationService {

            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public double getLatitude() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                double _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getLatitude, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readDouble();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public double getLongitude() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                double _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getLongitude, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readDouble();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public double getDistance() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                double _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getDistance, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readDouble();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public double getAverageSpeed() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                double _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getAverageSpeed, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readDouble();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
        }

        static final int TRANSACTION_getLatitude = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_getLongitude = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_getDistance = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_getAverageSpeed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    }

    public double getLatitude() throws android.os.RemoteException;

    public double getLongitude() throws android.os.RemoteException;

    public double getDistance() throws android.os.RemoteException;

    public double getAverageSpeed() throws android.os.RemoteException;
}
