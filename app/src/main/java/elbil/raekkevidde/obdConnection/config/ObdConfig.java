package elbil.raekkevidde.obdConnection.config;

import elbil.raekkevidde.obdJavaApi.commands.ObdCommand;
import elbil.raekkevidde.obdJavaApi.commands.SpeedCommand;
import elbil.raekkevidde.obdJavaApi.commands.control.DistanceMILOnCommand;
import elbil.raekkevidde.obdJavaApi.commands.control.DtcNumberCommand;
import elbil.raekkevidde.obdJavaApi.commands.control.EquivalentRatioCommand;
import elbil.raekkevidde.obdJavaApi.commands.control.ModuleVoltageCommand;
import elbil.raekkevidde.obdJavaApi.commands.control.TimingAdvanceCommand;
import elbil.raekkevidde.obdJavaApi.commands.control.TroubleCodesCommand;
import elbil.raekkevidde.obdJavaApi.commands.control.VinCommand;
import elbil.raekkevidde.obdJavaApi.commands.engine.LoadCommand;
import elbil.raekkevidde.obdJavaApi.commands.engine.MassAirFlowCommand;
import elbil.raekkevidde.obdJavaApi.commands.engine.OilTempCommand;
import elbil.raekkevidde.obdJavaApi.commands.engine.RPMCommand;
import elbil.raekkevidde.obdJavaApi.commands.engine.RuntimeCommand;
import elbil.raekkevidde.obdJavaApi.commands.engine.ThrottlePositionCommand;
import elbil.raekkevidde.obdJavaApi.commands.fuel.AirFuelRatioCommand;
import elbil.raekkevidde.obdJavaApi.commands.fuel.ConsumptionRateCommand;
import elbil.raekkevidde.obdJavaApi.commands.fuel.FindFuelTypeCommand;
import elbil.raekkevidde.obdJavaApi.commands.fuel.FuelLevelCommand;
import elbil.raekkevidde.obdJavaApi.commands.fuel.FuelTrimCommand;
import elbil.raekkevidde.obdJavaApi.commands.fuel.WidebandAirFuelRatioCommand;
import elbil.raekkevidde.obdJavaApi.commands.pressure.BarometricPressureCommand;
import elbil.raekkevidde.obdJavaApi.commands.pressure.FuelPressureCommand;
import elbil.raekkevidde.obdJavaApi.commands.pressure.FuelRailPressureCommand;
import elbil.raekkevidde.obdJavaApi.commands.pressure.IntakeManifoldPressureCommand;
import elbil.raekkevidde.obdJavaApi.commands.temperature.AirIntakeTemperatureCommand;
import elbil.raekkevidde.obdJavaApi.commands.temperature.AmbientAirTemperatureCommand;
import elbil.raekkevidde.obdJavaApi.commands.temperature.EngineCoolantTemperatureCommand;
import elbil.raekkevidde.obdJavaApi.enums.FuelTrim;

import java.util.ArrayList;

/**
 * TODO put description
 */
public final class ObdConfig {

    public static ArrayList<ObdCommand> getCommands() {
        ArrayList<ObdCommand> cmds = new ArrayList<>();

        // Control
        cmds.add(new ModuleVoltageCommand());
        cmds.add(new EquivalentRatioCommand());
        cmds.add(new DistanceMILOnCommand());
        cmds.add(new DtcNumberCommand());
        cmds.add(new TimingAdvanceCommand());
        cmds.add(new TroubleCodesCommand());
        cmds.add(new VinCommand());

        // Engine
        cmds.add(new LoadCommand());
        cmds.add(new RPMCommand());
        cmds.add(new RuntimeCommand());
        cmds.add(new MassAirFlowCommand());
        cmds.add(new ThrottlePositionCommand());

        // Fuel
        cmds.add(new FindFuelTypeCommand());
        cmds.add(new ConsumptionRateCommand());
        // cmds.add(new AverageFuelEconomyObdCommand());
        //cmds.add(new FuelEconomyCommand());
        cmds.add(new FuelLevelCommand());
        // cmds.add(new FuelEconomyMAPObdCommand());
        // cmds.add(new FuelEconomyCommandedMAPObdCommand());
        cmds.add(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_1));
        cmds.add(new FuelTrimCommand(FuelTrim.LONG_TERM_BANK_2));
        cmds.add(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_1));
        cmds.add(new FuelTrimCommand(FuelTrim.SHORT_TERM_BANK_2));
        cmds.add(new AirFuelRatioCommand());
        cmds.add(new WidebandAirFuelRatioCommand());
        cmds.add(new OilTempCommand());

        // Pressure
        cmds.add(new BarometricPressureCommand());
        cmds.add(new FuelPressureCommand());
        cmds.add(new FuelRailPressureCommand());
        cmds.add(new IntakeManifoldPressureCommand());

        // Temperature
        cmds.add(new AirIntakeTemperatureCommand());
        cmds.add(new AmbientAirTemperatureCommand());
        cmds.add(new EngineCoolantTemperatureCommand());

        // Misc
        cmds.add(new SpeedCommand());


        return cmds;
    }

}
