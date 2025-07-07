# BestMatch
Get the best prices of products across platforms.


### Steps to reproduce
```bash
# clone the repo
git clone https://github.com/hemang11/BestMatch.git
cd BestMatch

# Build the docker image and run the container
docker build -t bestmatch-scraper .
docker run --rm -p 9111:9111 bestmatch-scraper

# Test the API
curl -X POST "http://localhost:9111/rest/v1/best-match" \
-H "Content-Type: application/json" \
-d '{"country":"IN","query":"iPhone 16 Pro, 128GB"}'
```

#### Example request-reponse
**POST** `/rest/v1/best-match`  
**Body:**
```json
{
  "country": "IN",
  "query": "iPhone 16 Pro Max 128 GB"
}
```

**Sample Response**
```json
[
    {
        "url": "https://www.amazon.in/iPhone-16-128-GB-Control/dp/B0DGHZWBYB/ref=sr_1_14?dib=eyJ2IjoiMSJ9.Ka89qhdpabB3NoR8D6tI4TDkt7Om_BwH9gk80Dp5MpP-p12DaHLT2sIPB_UsatWibqjw4_ZXeh8urtC3lpwS1sxz1uQceP_QfwCrping_IlZwBHFYZdClwhsypNk4PZoyDH_QMAEy9-CowPP35qL-pk0p3fx3TJhRnMAZziPSkvf8AJKDcaYb51xGOw03jZQUg9nZS25HQ8thCg2-9L2ttXtPEcvlCHPmDm4XOZCHk8.c2hkt-FA0LStgZE7jU-SXC1l7aqyQa7OGOAwYUcBl1o&dib_tag=se&keywords=Iphone+16+Pro+Max+128+GB&qid=1751849116&sr=8-14",
        "price": 73500.0,
        "currency": "₹",
        "productName": "iPhone 16 128 GB: 5G Mobile Phone with Camera Control, A18 Chip and a Big Boost in Battery Life. Works with AirPods; White",
        "platform": "AMAZON"
    },
    {
        "url": "https://www.amazon.in/iPhone-16-128-Plus-Ultrmarine/dp/B0DGJ65N7V/ref=sr_1_16?dib=eyJ2IjoiMSJ9.Ka89qhdpabB3NoR8D6tI4TDkt7Om_BwH9gk80Dp5MpP-p12DaHLT2sIPB_UsatWibqjw4_ZXeh8urtC3lpwS1sxz1uQceP_QfwCrping_IlZwBHFYZdClwhsypNk4PZoyDH_QMAEy9-CowPP35qL-pk0p3fx3TJhRnMAZziPSkvf8AJKDcaYb51xGOw03jZQUg9nZS25HQ8thCg2-9L2ttXtPEcvlCHPmDm4XOZCHk8.c2hkt-FA0LStgZE7jU-SXC1l7aqyQa7OGOAwYUcBl1o&dib_tag=se&keywords=Iphone+16+Pro+Max+128+GB&qid=1751849116&sr=8-16",
        "price": 83500.0,
        "currency": "₹",
        "productName": "iPhone 16 Plus 128 GB: 5G Mobile Phone with Camera Control, A18 Chip and a Big Boost in Battery Life. Works with AirPods; Ultrmarine",
        "platform": "AMAZON"
    },
    {
        "url": "https://www.flipkart.com/search/apple-iphone-16-plus-ultramarine-128-gb/p/itm3a32a91957d41?pid=MOBH4DQFKZDYZNWX&lid=LSTMOBH4DQFKZDYZNWXWFOFLY&marketplace=FLIPKART&q=Iphone+16+Pro+Max+128+GB&store=tyy%2F4io&srno=s_1_3&otracker=search&fm=organic&iid=7e8812aa-a84b-493a-b117-aeab085bf424.MOBH4DQFKZDYZNWX.SEARCH&ppt=None&ppn=None&ssid=tmllge7hds0000001751849118797&qH=cb84a8754354dcf5",
        "price": 84900.0,
        "currency": "₹",
        "productName": "Apple iPhone 16 Plus (Ultramarine, 128 GB)",
        "platform": "FLIPKART"
    }
]
```

### Proof of work
![Demo1](proof/1.png)

![Demo2](proof/2.png)

![Demo3](proof/3.png)

![Demo4](proof/4.png)

