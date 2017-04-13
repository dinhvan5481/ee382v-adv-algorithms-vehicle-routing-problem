import matplotlib as plt
from matplotlib import pyplot
import pandas as pd


def read_vrp_instance(file_name):
    customers = {}
    with open(file_name) as file:
        lines = file.readlines()
        for line in lines:
            line_parts = line.split(' ')
            customers[int(line_parts[0])] = {'xCord': float(line_parts[1]), 'yCord': float(line_parts[2])}

    return customers


def read_routes(route_file_name):
    routes = []
    with open(route_file_name) as f:
        lines = f.readlines()
        for line in lines:
            line_parts = line.split(' ')
            routes.append(list(int(customer_id) for customer_id in line_parts))

    return list(routes)


def calculate_distance(from_customer, to_customer):
    return ((from_customer['xCord'] - to_customer['xCord']) ** 2 + (from_customer['yCord'] - to_customer['yCord']) ** 2) ** (1 / 2)


def calculate_route_cost(customers, routes):
    solution_cost = 0
    for route in routes:
        cost = 0
        for i in range(0, len(route) - 1):
            cost += calculate_distance(customers.get(route[i]), customers.get(route[i+1]))
        solution_cost += cost
        print('route: {} - cost: {}'.format(route, cost))

    print('Solution cost: {}'.format(solution_cost))

def main():
    vrp_instance_name = 'A-n37-k6.csv'
    customers = read_vrp_instance('../data/A-VRP/{}'.format(vrp_instance_name))
    routes = read_routes('../solutions/A-VRP/{}'.format(vrp_instance_name))
    fig = pyplot.figure()
    for route in routes:
        for index in range(len(route) - 1):
            customer_id_from = route[index]
            customer_id_to = route[index + 1]
            customer_from = customers[customer_id_from]
            customer_to = customers[customer_id_to]
            pyplot.plot([customer_from['xCord'], customer_to['xCord']], [customer_from['yCord'], customer_to['yCord']],
                        'yo')
            pyplot.plot([customer_from['xCord'], customer_to['xCord']], [customer_from['yCord'], customer_to['yCord']],
                        'b')

    pyplot.plot(customers[1]['xCord'], customers[1]['yCord'], 'rs')
    fig.savefig('test.pdf')
    return


if __name__ == '__main__':
    #main()
    vrp_instance_name = 'A-n37-k6.csv'
    customers = read_vrp_instance('../data/A-VRP/{}'.format(vrp_instance_name))
    routes = read_routes('../solutions/A-VRP/{}'.format(vrp_instance_name))

    calculate_route_cost(customers, routes)
