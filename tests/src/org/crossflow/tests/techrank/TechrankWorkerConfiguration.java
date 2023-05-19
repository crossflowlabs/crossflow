package org.crossflow.tests.techrank;

public class TechrankWorkerConfiguration {
    private String name;
    private long netSpeedBps;
    private long ioSpeedBps;

    public TechrankWorkerConfiguration() {

    }

    public TechrankWorkerConfiguration(String name, long netSpeedBps, long ioSpeedBps) {
        this.name = name;
        this.netSpeedBps = netSpeedBps;
        this.ioSpeedBps = ioSpeedBps;
    }

    public String getName() {
        return name;
    }

    public long getNetSpeedBps() {
        return netSpeedBps;
    }

    public long getIoSpeedBps() {
        return ioSpeedBps;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNetSpeedBps(long netSpeedBps) {
        this.netSpeedBps = netSpeedBps;
    }

    public void setIoSpeedBps(long ioSpeedBps) {
        this.ioSpeedBps = ioSpeedBps;
    }

    @Override
    public String toString() {
        return "TechrankWorkerConfiguration{" +
                "name='" + name + '\'' +
                ", netSpeedBps=" + netSpeedBps +
                ", ioSpeedBps=" + ioSpeedBps +
                '}';
    }
}
