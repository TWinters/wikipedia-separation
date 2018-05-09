import pprint

# new_file = open("./overview_communities.txt", "w")


def print_out_communities(input_file):
    total_dict = {}
    number = input_file[19:-4]
    with open(input_file, 'r') as infile:
        for line in infile:
            values = line.split(sep="\t")
            total_dict.setdefault(int(values[1]), []).append(int(values[0]))

    with open("./overview_communities_{}.txt".format(number), "w") as f:
        for key, value in total_dict.items():
            f.write('%s\t%s\n' % (key, value))

    with open("./overview_communities_{}_filtered.txt".format(number), "w") as f:
        for key, value in total_dict.items():
            if len(value) >= 5:
                f.write('%s\t%s\n' % (key, value))


def compute_degree_mode():
    degree_dict = {}

    with open("./page_links.csv", 'r') as infile:
        for line in infile:
            values = line.split(sep=",")
            for v in values:
                v = int(v)
                if v not in degree_dict:
                    degree_dict[v] = 1
                else:
                    degree_dict[v] += 1

    list_of_degrees = list(degree_dict.values())
    current_d = 0
    length_streak = 0
    mode = 0
    longest_streak = 0
    with open("./degree_distribution.txt", 'w') as outfile:
        for d in sorted(list_of_degrees):

            if current_d == d:
                length_streak += 1
            else:
                if length_streak > longest_streak:
                    longest_streak = length_streak
                    mode = current_d
                print("{} links \t {} amount of pages".format(current_d, length_streak))
                outfile.write("{} links \t {} amount of pages\n".format(current_d, length_streak))
                current_d = d
                length_streak = 1

    return mode


if __name__ == "__main__":
    for i in [1, 3, 8, 17]:
        print_out_communities("output_communities_{}.txt".format(i))
