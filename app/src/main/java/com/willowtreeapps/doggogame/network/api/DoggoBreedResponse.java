package com.willowtreeapps.doggogame.network.api;

import com.squareup.moshi.Json;

import java.util.List;

/*
JSON will look like this:

{
    status: "success",
    message: [
    "affenpinscher",
    "african",
    "airedale",
    "akita",
    "appenzeller",
    "basenji",
    "beagle",
    "bluetick",
    "borzoi",
    "bouvier",
    "boxer",
    "brabancon",
    "briard",
    "bulldog",
    "bullterrier",
    "cairn",
    "chihuahua",
    "chow",
    "clumber",
    "collie",
    "coonhound",
    "corgi",
    "dachshund",
    "dane",
    "deerhound",
    "dhole",
    "dingo",
    "doberman",
    "elkhound",
    "entlebucher",
    "eskimo",
    "germanshepherd",
    "greyhound",
    "groenendael",
    "hound",
    "husky",
    "keeshond",
    "kelpie",
    "komondor",
    "kuvasz",
    "labrador",
    "leonberg",
    "lhasa",
    "malamute",
    "malinois",
    "maltese",
    "mastiff",
    "mexicanhairless",
    "mountain",
    "newfoundland",
    "otterhound",
    "papillon",
    "pekinese",
    "pembroke",
    "pinscher",
    "pointer",
    "pomeranian",
    "poodle",
    "pug",
    "pyrenees",
    "redbone",
    "retriever",
    "ridgeback",
    "rottweiler",
    "saluki",
    "samoyed",
    "schipperke",
    "schnauzer",
    "setter",
    "sheepdog",
    "shiba",
    "shihtzu",
    "spaniel",
    "springer",
    "stbernard",
    "terrier",
    "vizsla",
    "weimaraner",
    "whippet",
    "wolfhound"
    ]
}

 */
class DoggoBreedResponse {
    static final String SUCCESS = "success";

    private String status;
    private @Json(name = "message")
    List<String> breeds;

    String getStatus() {
        return status;
    }

    List<String> getBreeds() {
        return breeds;
    }
}
