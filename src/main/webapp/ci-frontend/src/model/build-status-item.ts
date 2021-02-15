interface BuildStatusItemParameters {
    success: boolean,
    tests: number,
    testFailures: number,
    duration: string,
    timeEnd: Date,
    commitSHA: string,
}

export class BuildStatusItem {
    private param: BuildStatusItemParameters;

    constructor(param : BuildStatusItemParameters) {
        this.param = param;
    }

    public static fromJson(json: any) :  BuildStatusItem {
        return new BuildStatusItem({
            success: json.result == "BUILD SUCCESS" ? true : false,
            tests: json.statistics["Tests run"],
            testFailures: json.statistics["Failures"] * 1 + json.statistics["Errors"] * 1,
            duration: json.time,
            timeEnd: new Date(json.endTime),
            commitSHA: json.commitSHA,
        });
    }

    get success() {
        return this.param.success;
    }

    get duration() {
        return this.param.duration;
    }

    get timeEnd() {
        return this.param.timeEnd;
    }

    get commitSHA() {
        return this.param.commitSHA;
    }

    get timeEndHMS() {
        return ("0" + this.timeEnd.getHours()).slice(-2) + 
        ":" + ("0" + this.timeEnd.getMinutes()).slice(-2) + 
        ":" + ("0" + this.timeEnd.getSeconds()).slice(-2);
    }

    get timeEndDMY() {
        return ("0" + this.timeEnd.getDate()).slice(-2) + "-" + ("0" + (this.timeEnd.getMonth() + 1)).slice(-2) + "-" + this.timeEnd.getFullYear();
    }

    get numberOfTestsPassed() {
        return this.param.tests - this.param.testFailures;
    }

    get numberOfTestsFailed() {
        return this.param.testFailures;
    }
}