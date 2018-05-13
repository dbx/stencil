#!/bin/env ruby

puts (`lein clean` or abort("Could not clean up. No leiningen?"))
puts (`lein pom` or abort("Could not make pom file"))
puts (`lein uberjar` or abort("Could not compile!"))
# puts (`mvn package -DskipTests` or abort("Could not make jar package"))

JARFILE = Dir.glob("target/stencil-*.jar").find { |fn| not fn.include? "-standalone" }
UBERJARFILE = Dir.glob("target/stencil-*-standalone.jar").first
POMFILE = "pom.xml"

abort('Missing jar file, build it first!') unless JARFILE

GROUP = "io/github/erdos"
ARTIFACT = "stencil"
VERSION = /stencil-(.*).jar/.match(JARFILE)[1]

puts "Csoport: #{GROUP}"
puts "Artifakt: #{ARTIFACT}"
puts "Verzio: #{VERSION}"

REMOTE_DIRECTORY="/home/ubuntu/maven/repository/#{GROUP}/#{ARTIFACT}/#{VERSION}"
puts (`ssh dbx.services mkdir -p #{REMOTE_DIRECTORY}` or abort("Could not create dir"))

REMOTE_UBERJAR_FILE="#{REMOTE_DIRECTORY}/stencil-#{VERSION}-standalone.jar"
puts (`scp #{UBERJARFILE} dbx.services:#{REMOTE_UBERJAR_FILE}` or abort("Could not copy UBERJAR!"))

REMOTE_JAR_FILE="#{REMOTE_DIRECTORY}/stencil-#{VERSION}.jar"
puts (`scp #{JARFILE} dbx.services:#{REMOTE_JAR_FILE}` or abort("Could not copy JAR!"))

REMOTE_POM_FILE="#{REMOTE_DIRECTORY}/stencil-#{VERSION}.pom"
puts (`scp #{POMFILE} dbx.services:#{REMOTE_POM_FILE}` or abort("Could not copy POM!"))

puts "Good!"
