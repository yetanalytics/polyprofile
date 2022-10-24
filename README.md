# polyprofile

xAPI Profile generation library

## Installation

```clojure
{com.yetanalytics/polyprofile {:mvn/version "0.1.2"
                               :exclusions [org.clojure/clojure
                                            org.clojure/clojurescript]}}
```

## API

The API has a single function: `generate-profile-seq`, which takes a single arg map and generates a lazy seq of xAPI Profiles whose IRI properties (e.g. `broader`, `narrower`) are guaranteed to relate to other Profile objects in that seq.

Options not provided in the arg map use the values of `default-args`. The following is a list of valid args:
| Arg | Description | Default |
| --- | --- | --- |
| `:num-profiles` | The number of Profiles to be generated in the seq. | `10` |
| `:num-versions` | The number of Profile versions per Profile. | `2` |
| `:num-verbs` | The number of Verbs per Profile. | `5` |
| `:num-activity-types` | The number of Activity Types per Profile. | `5` |
| `:num-attachment-usage-types` | The number of Attachment Usage Types per Profile. | `2` |
| `:num-activity-extensions` | The number of Activity Extensions per Profile. | `0` |
| `:num-context-extensions` | The number of Context Extensions per Profile. | `0` |
| `:num-result-extensions` | The number of Result Extensions per Profile. | `0` |
| `:num-state-resources` | The number of State Resources per Profile. | `0` |
| `:num-activity-profile-resources` | The number of Activity Profile Resources per Profile. | `0` |
| `:num-agent-profile-resources` | The number of Agent Profile Resources per Profile. | `0` |
| `:num-activities` | The number of Activities per Profile. | `5` |
| `:num-statement-templates` | The number of Statement Templates per Profile. | `5` |
| `:num-patterns` | The number of Patterns per Profile. | `5` |
| `:max-iris` | The maximum number of IRIs that can be generated in a single IRI array (e.g. for `broader` and `narrower`). | `5` |

All IRIs will point to other IRIs in the Profile cosmos to the extent specified by the xAPI Profile spec; the exception is with Patterns, which only point to sub-Patterns in the same Profile version (though they can point to any Template in the cosmos).
