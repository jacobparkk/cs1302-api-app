# Deadline

Modify this file to satisfy a submission requirement related to the project
deadline. Please keep this file organized using Markdown. If you click on
this file in your GitHub repository website, then you will see that the
Markdown is transformed into nice-looking HTML.

## Part 1.1: App Description

> Please provide a friendly description of your app, including
> the primary functions available to users of the app. Be sure to
> describe exactly what APIs you are using and how they are connected
> in a meaningful way.

> **Also, include the GitHub `https` URL to your repository.**

My ApiApp, Top Team Wiki Finder, is designed for the user to select a top 5
football (soccer) league from the dropdown menu and then input any valid year
that the league was played. Then, the app would display who was the winning team
of that league that year (the year inputted is the year the season started
EX: 2020 -> 2020-2021). Finally, the team that won the league that year would
then be used in a Wikipedia search and using webView, a Wikipedia info page of
that team would be displayed.

## Part 1.2: APIs

> For each RESTful JSON API that your app uses (at least two are required),
> include an example URL for a typical request made by your app. If you
> need to include additional notes (e.g., regarding API keys or rate
> limits), then you can do that below the URL/URI. Placeholders for this
> information are provided below. If your app uses more than two RESTful
> JSON APIs, then include them with similar formatting.

### API 1
```
https://api-football-standings.azharimm.site/leagues/eng.1/standings?season=2020&sort=asc
```

> Replace this line with notes (if needed) or remove it (if not needed).

### API 2

```
https://en.wikipedia.org/w/rest.php/v1/search/page?q=Chelsea+F.C.&limit=1"
```

> Replace this line with notes (if needed) or remove it (if not needed).

## Part 2: New

> What is something new and/or exciting that you learned from working
> on this project?

I learned everything about webView. It was similar to imageView, so it was not a totally new
thing, however, webView allows websites and other file types to be displayed. This made me feel
like I was becoming a real-life coder as I was able to obtain things from the internet and code
it all myself.

## Part 3: Retrospect

> If you could start the project over from scratch, what do
> you think might do differently and why?

I attempted to use the GIPHY api to print a gif of the top team instead of the wiki page, however,
I was unable to figure out how to display a gif through webView because there was an error. So if I
were to restart the project over, I would do more research about the APIs and see if they are compatible
with what I want to accomplish.
