
# Plot Title
set title "Clustering coefficient"

# Axes label
set xlabel 'cycles'
set ylabel 'clustering coefficient'

# Line width of the axes
set border linewidth 1.5

# Tics
set xtics 5

# Line styles
unset style line
set style line 1 linecolor rgb '#0060ad' linetype 1 linewidth 1

# Output format and file name
set terminal png size 800,400 enhanced font 'Helvetica,11'
set output 'cc.png'

plot 'output.log' with linespoints linestyle 1
