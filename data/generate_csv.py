import csv
import random

# --- Configuration ---
NUM_ROWS = 5000000
OUTPUT_FILE = 'large_input.csv'
NUM_UNIQUE_USERS = 10000

# --- Data Pools ---

# Create a pool of user IDs
user_ids = [f'user_{i}' for i in range(1, NUM_UNIQUE_USERS + 1)]
# Add some "power users" that will post more frequently
power_users = ['user_101', 'user_202', 'user_303']
user_ids.extend(power_users * 10) # Make them 10x more likely to be chosen

# Create a pool of messages
common_messages = [
    "This is a test message.",
    "Having a wonderful day!",
    "I agree with the points made above.",
    "What's everyone having for lunch?",
    "Just checking in.",
    "The weather in Madrid is lovely today.",
    "Thinking about my next vacation.",
]

spam_messages = [
    "CHECK OUT THIS AMAZING DEAL NOW!!!",
    "Click here for a free prize, you won't regret it!",
    "Limited time offer, subscribe immediately.",
    " ",
    "Lorem Impsum",
]

complex_messages = [
    "This message, containing a comma, should be handled correctly.",
    'She said, ""This is a quote inside a message!"" and it was great.',
    "Let's discuss A, B, and C.",
    "",
]

# Combine all messages, making spam more frequent
all_messages = common_messages + complex_messages + (spam_messages * 15)

# --- CSV Generation ---

print(f"Generating {NUM_ROWS} rows for '{OUTPUT_FILE}'...")

with open(OUTPUT_FILE, 'w', newline='', encoding='utf-8') as csvfile:
    writer = csv.writer(csvfile)

    # Write the header
    writer.writerow(['user_id', 'message'])

    # Write the data rows
    for _ in range(NUM_ROWS):
        user_id = random.choice(user_ids)
        message = random.choice(all_messages)
        writer.writerow([user_id, message])

print("Done. CSV file has been generated successfully!")