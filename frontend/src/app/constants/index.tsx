import { TestResult } from "../testQueue/QueueItem";

export const COVID_RESULTS: { [key: string]: TestResult } = {
  POSITIVE: "POSITIVE",
  NEGATIVE: "NEGATIVE",
  INCONCLUSIVE: "UNDETERMINED",
};

export const TEST_RESULT_VALUES = {
  0: COVID_RESULTS.NEGATIVE,
  1: COVID_RESULTS.POSITIVE,
  2: COVID_RESULTS.INCONCLUSIVE,
};

export const TEST_RESULT_DESCRIPTIONS = {
  NEGATIVE: "Negative",
  POSITIVE: "Positive",
  UNDETERMINED: "Inconclusive",
};

export const RACE_VALUES = [
  {
    value: "native",
    label: "American Indian or Alaskan Native",
  },
  {
    value: "asian",
    label: "Asian",
  },
  {
    value: "black",
    label: "Black or African American",
  },
  {
    value: "pacific",
    label: "Native Hawaiian or other Pacific Islander",
  },
  {
    value: "white",
    label: "White",
  },
  {
    value: "unknown",
    label: "Unknown",
  },
  {
    value: "refused",
    label: "Refused to Answer",
  },
];

export const ETHNICITY_VALUES = [
  { label: "Hispanic or Latino", value: "hispanic" },
  { label: "Not Hispanic", value: "not_hispanic" },
];
export const GENDER_VALUES = [
  { label: "Male", value: "male" },
  { label: "Female", value: "female" },
  { label: "Other", value: "other" },
];
