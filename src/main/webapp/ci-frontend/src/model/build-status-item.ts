interface BuildStatusParameters {
    success: boolean,
    timeStart: Date,
    timeEnd: Date,
    buildNumber: string,
}

export class BuildStatus {
    private param: BuildStatusParameters;
    constructor(param : BuildStatusParameters) {
        this.param = param;
    }

    get success() {
        return this.param.success;
    }
    get timeStart() {
        return this.param.timeStart;
    }
    get timeEnd() {
        return this.param.timeEnd;
    }
    get buildNumber() {
        return this.param.buildNumber;
    }

    get timeEndHMS() {
        return ("0" + this.timeEnd.getHours()).slice(-2) + 
        ":" + ("0" + this.timeEnd.getMinutes()).slice(-2) + 
        ":" + ("0" + this.timeEnd.getSeconds()).slice(-2);
    }
    get timeEndDMY() {
        return ("0" + this.timeEnd.getDate()).slice(-2) + "-" + ("0" + (this.timeEnd.getMonth() + 1)).slice(-2) + "-" + this.timeEnd.getFullYear();
    }
}