/**
 *  Child Humidity Sensor
 *
 *  https://raw.githubusercontent.com/DanielOgorchock/ST_Anything/master/HubDuino/Drivers/child-humidity-sensor.groovy
 *
 *  Copyright 2017 Daniel Ogorchock
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2017-04-10  Dan Ogorchock  Original Creation
 *    2017-08-23  Allan (vseven) Added a generateEvent routine that gets info from the parent device.  This routine runs each time the value is updated which can lead to other modifications of the device.
 *    2017-08-24  Allan (vseven) Added a lastUpdated attribute that will display on the multitile.
 *    2017-09-09  Allan (vseven) Added preference to offset the humidity.
 *    2018-06-02  Dan Ogorchock  Revised/Simplified for Hubitat Composite Driver Model
 *    2018-09-22  Dan Ogorchock  Added preference for debug logging
 *    2019-03-05  Dan Ogorchock  Improved Rounding
 *    2019-07-01  Dan Ogorchock  Added importUrl
 *    2019-10-30  Dan Ogorchock  Fixed type conversion error found by @kuzenkohome
 *    2020-01-25  Dan Ogorchock  Remove custom lastUpdated attribute & general code cleanup
 *
 * 
 */
metadata {
    definition (name: "Child Humidity Sensor", namespace: "ogiewon", author: "Daniel Ogorchock", importUrl: "https://raw.githubusercontent.com/DanielOgorchock/ST_Anything/master/HubDuino/Drivers/child-humidity-sensor.groovy") {
        capability "Relative Humidity Measurement"
        capability "Sensor"
    }
    
    preferences {
        section("Prefs") {
            input "humidityOffset", "number", title: "Humidity Offset in Percent", description: "Adjust humidity by this percentage", range: "*..*", displayDuringSetup: false
            input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
        }
    }
}

def logsOff(){
    log.warn "debug logging disabled..."
    device.updateSetting("logEnable",[value:"false",type:"bool"])
}

def parse(String description) {
    if (logEnable) log.debug "parse(${description}) called"
    def parts = description.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null
    if (name && value) {
        // Offset the humidity based on preference
        float tmpValue = Float.parseFloat(value)
        if (humidityOffset) {
            tmpValue = tmpValue + humidityOffset.toFloat()
        }
        // Update device
        tmpValue = tmpValue.round(1)
        sendEvent(name: name, value: tmpValue)
    }
    else {
    	log.error "Missing either name or value.  Cannot parse!"
    }
}

def installed() {
    updated()
}

def updated() {
    if (logEnable) runIn(1800,logsOff)
}
