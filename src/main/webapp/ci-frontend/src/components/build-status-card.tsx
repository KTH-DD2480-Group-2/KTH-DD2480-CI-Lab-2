import React, { FunctionComponent } from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import { BuildStatus } from '../model/build-status-item';

const useStyles = makeStyles({
  root: {
    width: "100%",
  },
  bullet: {
    display: 'inline-block',
    margin: '0 2px',
    transform: 'scale(0.8)',
  },
  title: {
    fontSize: 14,
  },
  pos: {
    marginBottom: 12,
  },
});

type BuildStatusCardProps = {
    buildStatus: BuildStatus,
}
  

export const BuildStatusCard: FunctionComponent<BuildStatusCardProps> = ({ buildStatus }) =>  {
  const classes = useStyles();
  const bull = <span className={classes.bullet}>•</span>;

  return (
    <Card elevation={3} className={classes.root}>
      <CardContent>
        <Typography className={classes.title} color="textSecondary" gutterBottom>
            Commit: <a href={"https://github.com/" + buildStatus.buildNumber}>{buildStatus.buildNumber}</a> 
        </Typography>
        <Typography variant="h6" component="h5">
          {
              buildStatus.success ? "Build Succeeded" : "Build Failed"
          }
        </Typography>
        <Typography className={classes.pos} color="textSecondary">
          Took 394 seconds
        </Typography>
        <Typography variant="body2" component="p">
          TODO: Add more info here if needed
        </Typography>
      </CardContent>
    </Card>
  );
}
