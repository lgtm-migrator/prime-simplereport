import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React from "react";
import { IconProp } from "@fortawesome/fontawesome-svg-core";

interface Props {
  icon: IconProp;
  primaryText: string;
  secondaryText?: string;
}
// todo: fix spacing on secondary text
export const IconLabel = (props: Props) => (
  <div className={"display-flex"}>
    <div>
      <FontAwesomeIcon icon={props.icon} />
    </div>
    <div className={"margin-left-105"}>
      <span className={"font-sans-sm text-primary"}>
        {props.primaryText}
        <div className={"font-sans-3xs text-base margin-top-05"}>
          {props.secondaryText}
        </div>
      </span>
    </div>
  </div>
);
