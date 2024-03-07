import json
import requests
from faker import Faker
from multiprocessing import Pool


def generate_user_basic():
    fake = Faker()
    return {
        "validCode": '123',
        "pwd": '123',
        "userBasic": {
            "userEmail": fake.email()
        }
    }


def post_request(num_requests, url):
    results = []
    payloads = [generate_user_basic() for _ in range(num_requests)]
    for payload in payloads:
        json_payload = json.dumps(payload)
        headers = {'Content-Type': 'application/json'}
        response = requests.post(url, data=json_payload, headers=headers)
        if response.status_code == 200 and response.json()['success'] is True:
            results.append(response.json()['data'])
        else:
            print('error')


def post_random_user_data(pct, url, req_cnt=200):
    pool = Pool(processes=pct)  # You can adjust the number of processes as needed
    results = []
    for i in range(pct):
        results.append(pool.apply_async(post_request, (req_cnt, url)))
    pool.close()
    pool.join()

    for i, result in enumerate(results):
        datas = result.get()
        for data in datas:
            if data:
                print(f"第 {i + 1} 次请求成功，响应数据: {data}")
            else:
                print(f"第 {i + 1} 次请求失败")


if __name__ == "__main__":
    post_random_user_data(3, 'http://localhost:8078/api/v1/userInfo/register',1)
