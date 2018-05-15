import matplotlib.pyplot as plt
import numpy as np
from scipy.stats import mode
from pathlib import Path

# SCRIPT CONFIG
INPUT_FILE = Path("./page_links.csv")
COMMUNITIES_FILE = Path("./output_communities.txt")
MIN_COM_SIZE = 1


# Plot of the outgoing vs incoming links
def plot_incoming_outgoing(input_file: Path):
    incoming_dict = {}
    outgoing_dict = {}
    dicts = [outgoing_dict, incoming_dict]
    with input_file.open(mode='r') as infile:
        for line in infile:
            values = line.split(sep=",")
            for i, v in enumerate(values):
                v = int(v)
                if v not in dicts[i]:
                    dicts[i][v] = 1
                else:
                    dicts[i][v] += 1

    key_list = incoming_dict.keys()
    tuple_list = []
    for key in sorted(key_list):
        if key in outgoing_dict.keys():
            tuple_list.append((outgoing_dict[key], incoming_dict[key]))

    x, y = zip(*tuple_list)
    ax = plt.subplot(111)
    ax.set_yscale("log")
    ax.set_xscale("log")
    ax.set_ylim(ymin=1, ymax=1000000)
    ax.set_xlim(xmin=1, xmax=1000000)
    plt.scatter(x, y)
    plt.show()


# Prints out an overview of the communities
def overview_communities(input_file: Path, minimum: int):
    total_dict = {}
    with input_file.open(mode='r') as infile:
        for line in infile:
            values = line.split(sep="\t")
            total_dict.setdefault(int(values[1]), []).append(int(values[0]))

    with open("./overview_communities.csv", "w") as outfile:
        for key, value in total_dict.items():
            if len(value) >= minimum:
                outfile.write('%s,%s\n' % (key, value))


# The input for SCoDA is a tab separated txt file
def csv_to_txt(input_file: Path):
    output_file = "./input_graph.txt"
    outfile = open(output_file, 'w')
    with input_file.open(mode='r') as infile:
        for line in infile:
            values = line.split(sep=",")
            outfile.write("{}\t{}\n".format(int(values[0]), int(values[1])))
    return output_file


# Calculate the degree mode for the input into the SCoDA algorithm
def compute_degree_mode(input_file: Path, max_node_id: int):
    # Index as id maps to degree
    degrees = [0 for _ in range(max_node_id)]
    with open(input_file, 'r') as infile:
        for line in infile:
            values = line.split(sep=",")
            for v in values:
                degrees[int(v)] += 1
    degrees[degrees == 0] = np.nan
    return mode(degrees, nan_policy='omit')


# Finds the max node id for SCoDA
def find_max_node_id(input_file: Path):
    with input_file.open(mode='r') as infile:
        return max([int(v) for line in infile for v in line.split(sep=",")])


if __name__ == "__main__":
    if INPUT_FILE.exists():
        csv_to_txt(INPUT_FILE)
        max_id = find_max_node_id(INPUT_FILE)
        print("The largest found node id is {}.".format(str(max_id)))
        mode = compute_degree_mode(INPUT_FILE, max_id)
        print("The most occurring degree is {}.".format(str(mode)))
        if COMMUNITIES_FILE.exists():
            overview_communities(COMMUNITIES_FILE, MIN_COM_SIZE)
        plot_incoming_outgoing(INPUT_FILE)
