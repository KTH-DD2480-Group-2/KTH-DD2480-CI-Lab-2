import { SystemUpdate } from "@material-ui/icons";
import { BuildStatus } from "./build-status-item";

export class BuildStatusProvider {
    static async getAllBuildsFromCIServer() : Promise<BuildStatus[]> {
        // Dummy promise
        await new Promise( resolve => setTimeout(resolve, 2000) );
        return [
            new BuildStatus({
                success: true,
                timeStart: new Date(Date.now()-1000*60*10),
                timeEnd: new Date(Date.now()-1000*60*5),
                buildNumber: "asdfadwefsfdedf",
            }),
            new BuildStatus({
                success: false,
                timeStart: new Date(Date.now()-1000*60*15),
                timeEnd: new Date(Date.now()-1000*60*8),
                buildNumber: "asdgffsgfw4trdsdf",
            }),
            new BuildStatus({
                success: true,
                timeStart: new Date(Date.now()-1000*60*20),
                timeEnd: new Date(Date.now()-1000*60*10),
                buildNumber: "asdfg54sdfgsdf",
            }),
            new BuildStatus({
                success: false,
                timeStart: new Date(Date.now()-1000*60*30),
                timeEnd: new Date(Date.now()-1000*60*15),
                buildNumber: "4t5egrefsdgsed",
            }),
            new BuildStatus({
                success: false,
                timeStart: new Date(Date.now()-1000*60*35),
                timeEnd: new Date(Date.now()-1000*60*20),
                buildNumber: "45ythgsfaesgfthge",
            }),
            new BuildStatus({
                success: true,
                timeStart: new Date(Date.now()-1000*60*40),
                timeEnd: new Date(Date.now()-1000*60*25),
                buildNumber: "juytrefsg5ewsfd",
            }),
        ]
    }
}