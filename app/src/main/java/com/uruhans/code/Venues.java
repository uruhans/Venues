package com.uruhans.code;

/**
 * Created by uruha on 23-02-2016.
 * Pojos created by : https://timboudreau.com/blog/json/read
 */
public final class Venues {
    public final Meta meta;
    public final Response response;

    public Venues(Meta meta, Response response){
        this.meta = meta;
        this.response = response;
    }

    public static final class Meta {
        public final long code;
        public final String requestId;

        public Meta(long code, String requestId){
            this.code = code;
            this.requestId = requestId;
        }
    }

    public static final class Response {
        public final Venue venues[];

        public Response(Venue[] venues){
            this.venues = venues;
        }

        public static final class Venue {
            public final String id;
            public final String name;
            public final Contact contact;
            public final Location location;
            public final Category categories[];
            public final boolean verified;
            public final Stats stats;
            public final boolean allowMenuUrlEdit;
            public final Specials specials;
            public final HereNow hereNow;
            public final String referralId;
            public final VenueChain venueChains[];

            public Venue(String id, String name, Contact contact, Location location, Category[] categories, boolean verified, Stats stats, boolean allowMenuUrlEdit, Specials specials, HereNow hereNow, String referralId, VenueChain[] venueChains){
                this.id = id;
                this.name = name;
                this.contact = contact;
                this.location = location;
                this.categories = categories;
                this.verified = verified;
                this.stats = stats;
                this.allowMenuUrlEdit = allowMenuUrlEdit;
                this.specials = specials;
                this.hereNow = hereNow;
                this.referralId = referralId;
                this.venueChains = venueChains;
            }

            public static final class Contact {
                public final String phone;
                public final String formattedPhone;

                public Contact(String phone, String formattedPhone){
                    this.phone = phone;
                    this.formattedPhone = formattedPhone;
                }
            }

            public static final class Location {
                public final String address;
                public final String crossStreet;
                public final double lat;
                public final double lng;
                public final long distance;
                public final String postalCode;
                public final String cc;
                public final String city;
                public final String state;
                public final String country;
                public final String[] formattedAddress;

                public Location(String address, String crossStreet, double lat, double lng, long distance, String postalCode, String cc, String city, String state, String country, String[] formattedAddress){
                    this.address = address;
                    this.crossStreet = crossStreet;
                    this.lat = lat;
                    this.lng = lng;
                    this.distance = distance;
                    this.postalCode = postalCode;
                    this.cc = cc;
                    this.city = city;
                    this.state = state;
                    this.country = country;
                    this.formattedAddress = formattedAddress;
                }
            }

            public static final class Category {
                public final String id;
                public final String name;
                public final String pluralName;
                public final String shortName;
                public final Icon icon;
                public final boolean primary;

                public Category(String id, String name, String pluralName, String shortName, Icon icon, boolean primary){
                    this.id = id;
                    this.name = name;
                    this.pluralName = pluralName;
                    this.shortName = shortName;
                    this.icon = icon;
                    this.primary = primary;
                }

                public static final class Icon {
                    public final String prefix;
                    public final String suffix;

                    public Icon(String prefix, String suffix){
                        this.prefix = prefix;
                        this.suffix = suffix;
                    }
                }
            }

            public static final class Stats {
                public final long checkinsCount;
                public final long usersCount;
                public final long tipCount;

                public Stats(long checkinsCount, long usersCount, long tipCount){
                    this.checkinsCount = checkinsCount;
                    this.usersCount = usersCount;
                    this.tipCount = tipCount;
                }
            }

            public static final class Specials {
                public final long count;
                public final Item items[];

                public Specials(long count, Item[] items){
                    this.count = count;
                    this.items = items;
                }

                public static final class Item {

                    public Item(){
                    }
                }
            }

            public static final class HereNow {
                public final long count;
                public final String summary;
                public final Group groups[];

                public HereNow(long count, String summary, Group[] groups){
                    this.count = count;
                    this.summary = summary;
                    this.groups = groups;
                }

                public static final class Group {

                    public Group(){
                    }
                }
            }

            public static final class VenueChain {

                public VenueChain(){
                }
            }
        }
    }
}