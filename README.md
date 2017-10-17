# SocialMedia4Crisis
The Social Media for Crisis platform (SM4C) enables the monitoring of crisis related Social Media messages. It allows you to filter Social Media messages automatically by any kind of topic as well as relate the contents of those messages to its' position on earth. So the platform contributes to situational awareness and can be used by emergency responders to get a better understanding of the situation on the ground .

Note that this platform is still work in progress and some of the functionalities may be revised further.

## The modules

* `common`: Represents the database model of the SM4C platform and consists of a Hibernate layer for accessing the data.
* `collect`: Collector for Social Media messages. At this time there are routines for collecting groups, pages and posts from the Facebook platform. There is also a simple receiver and handler for collecting and processing the latest messages periodically.
* `classify`: Provides filter functionality for Social Media messages by the use of machine learning techniques. A text classification model can be trained by labeling some training data with custom topics. Subesequently this model will be used for the filtering task.
* `geotag`: Enables the geocoding of text messages. Natural Language Processing tools are used for geoparsing the messages to find toponyms. A geocoding routine disambiguates these toponyms to relate geographical coordinates to it.
* `rest`: A rest interface to use the different modules.
* `monitoring`: A prototypcial web app to utilize the different modules and monitor crisis related Social Media messages on a map.

