#!/usr/bin/env python
import csv
import json
import logging
import os
import requests
import sys
import time

logging.basicConfig(level=logging.INFO, format="%(lineno)d\t%(message)s")


def post_to_client(payload,
                   endpoint='http://localhost:8080/transactions',
                   headers={'content-type': 'application/json'}):

    payload_as_json = json.dumps(payload)
    logging.debug("payload is {p} and endpoint is {e}".format(p=payload_as_json,
                                                              e=endpoint))

    r = requests.post(endpoint, data=payload_as_json, headers=headers)
    logging.info("response: {0}".format(r.status_code))


def build_posts(csv_filepath):
    f = open(filepath, 'rU')
    fields = ('date', 'amount', 'asterisk', 'check', 'description')
    reader = csv.DictReader(f, fieldnames=fields)
    return map(format_row, [row for row in reader])


def format_date(date_string):
    (month, day, year) = date_string.split('/')
    return "{0}-{1}-{2}".format(year, month, day)


def format_row(row):
    return {'id': 0,
            'description': row['description'].replace("'", "\\'"),
            'date': format_date(row['date']),
            'category': 'unknown',
            'amount': float(row['amount'])}


def main(filepath,
         limit=0,
         endpoint='http://localhost:8080/transactions'):
    posts = build_posts(filepath)
    if limit > 0:
        posts = posts[:limit]

    for post in posts:
        time.sleep(1)
        post_to_client(post, endpoint)

if __name__ == '__main__':
    try:
        filepath = sys.argv[1]
        limit = int(sys.argv[2])
        main(filepath, limit)
    except Exception, err:
        logging.error("error: {e}".format(e=err))
        print "usage: {0} <filepath>".format(os.path.basename(sys.argv[0]))
        sys.exit(1)
