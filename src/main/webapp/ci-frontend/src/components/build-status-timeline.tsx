import React, { FunctionComponent } from "react"
import { BuildStatusItem } from "../model/build-status-item"
import Timeline from '@material-ui/lab/Timeline';
import TimelineItem from '@material-ui/lab/TimelineItem';
import TimelineSeparator from '@material-ui/lab/TimelineSeparator';
import TimelineConnector from '@material-ui/lab/TimelineConnector';
import TimelineContent from '@material-ui/lab/TimelineContent';
import TimelineDot from '@material-ui/lab/TimelineDot';
import { TimelineOppositeContent } from '@material-ui/lab';
import { BuildStatusCard } from "./build-status-card";

type BuildStatusTimelineProps = {
    builds: BuildStatusItem[],
}
  
export const BuildStatusTimeline: FunctionComponent<BuildStatusTimelineProps> = ({ builds }) => <div>
    <Timeline align="right">
        {
            builds.map(build => {
                return <TimelineItem key={build.commitSHA}>
                    <TimelineOppositeContent>
                        <BuildStatusCard buildStatus={build}/>
                    </TimelineOppositeContent>
                    <TimelineSeparator>
                        <TimelineConnector />
                        <TimelineDot variant="outlined" style={{
                            borderColor: build.success ? "#00aa00" : "#ff0000"
                        }} />
                        <TimelineConnector />
                    </TimelineSeparator>
                    <TimelineContent style={{flex: "none", alignItems: "center", display: "flex"}}>
                        <div>
                            <i>{build.timeEndDMY}</i>
                            <br/>
                            <b>{build.timeEndHMS}</b>
                        </div>
                    </TimelineContent>
                </TimelineItem>
            })
        }
    </Timeline>
</div>